using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Document = WebAppAC.Models.Document;
using System.Diagnostics;
using WebAppAC.Models;
using WebAppAC.Repositories;
using Google.Cloud.Firestore;
using System.Security.Claims;
using Microsoft.CodeAnalysis;
using Aspose.Words;

namespace WebAppAC.Controllers
{
    public class HomeController : Controller
    {
        private readonly ILogger<HomeController> _logger;
        private BucketsRepository _bucketsRepository;
        private DocumentsRepository _documentsRepository;
        private PubSubRepository _pubSubRepository;
        private UsersRepository _usersRepository;
        private RedisRepository _redisRepository;
        private IConfiguration _config;

        string idDoc;
        string idUser;

        public HomeController(ILogger<HomeController> logger, DocumentsRepository documentsRepository, 
            BucketsRepository bucketsRepository, IConfiguration config, PubSubRepository pubSubRepository, 
            UsersRepository usersRepository, RedisRepository redisRepository)
        {
            _logger = logger;
            _documentsRepository = documentsRepository;
            _bucketsRepository = bucketsRepository;
            _pubSubRepository = pubSubRepository;
            _usersRepository = usersRepository;
            _redisRepository = redisRepository;
            _config = config;

        }

        public async Task<IActionResult> Index([FromServices] RedisRepository rr)
        {
            var documents = await _documentsRepository.GetDocuments(HttpContext);
            var menus = _redisRepository.GetMenus();
            ViewData["Menus"] = menus;
            return View(documents);
        }

        [HttpGet]
        [Authorize]
        public IActionResult Create() { return View(); }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> Create(Document d, User u, IFormFile file)
        {
            idDoc = Guid.NewGuid().ToString();
            idUser = Guid.NewGuid().ToString();

            string bucketName = _config["bucket"];
            string uniqueFilename = idDoc + System.IO.Path.GetExtension(file.FileName);


            if (Path.GetExtension(file.FileName).ToLower() != ".docx")
            {
                ModelState.AddModelError("File", "Only DOCX files are supported.");
                return View();
            }

            MemoryStream userFile = new MemoryStream();
            file.CopyTo(userFile);

            await _bucketsRepository.UploadFile(uniqueFilename, userFile);

            // Get the logged-in user's email address from claims
            string recipient = User.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Email)?.Value;

            if (string.IsNullOrEmpty(recipient))
            {
                // Handle the case where email is not found in claims
                return BadRequest("User email not found.");
            }

            d.File = $"https://storage.cloud.google.com/{bucketName}/{uniqueFilename}";
            
            d.Id = idDoc;
            d.DateUploaded = Timestamp.FromDateTime(DateTime.UtcNow);
            d.Author = User.Identity.Name;
            d.Recipient = recipient;
            d.UserId = idUser;
            d.Status = "0"; //Pending Conversion


            u.Recipient = recipient;
            u.Id = idUser;
            u.DocumentId = idDoc;

            // Call Generate after document upload
            string fileName = idDoc + ".pdf";
            string testName = idDoc + ".docx";

            //Starting the Conversion
            await _pubSubRepository.Publish(idDoc, recipient);

            d.PDF = $"https://storage.cloud.google.com/{bucketName}/{fileName}";

            TempData["message"] = "Document Uploaded";

            _documentsRepository.AddDocument(d);

            return View();
        }


        [HttpGet]
        [Authorize]
        public async Task<IActionResult> Share(string documentId, string userId)
        {
            ViewData["DocumentId"] = documentId;
            ViewData["UserId"] = userId;

            return View();
        }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> Share(User u, Document d, string recipient, string documentId, string userId, IFormFile file)
        {          
            d.Recipient = recipient;

            //Passing the document ID from the Share view
            ViewData["DocumentId"] = documentId;
            ViewData["UserId"] = userId;
            u.Id = userId;

            // Retrieve the recipients array from Firestore
            List<string> firestoreRecipients = await _usersRepository.GetRecipientsFromFirestore(userId, documentId);

            // Check if the retrieved recipients array is null or empty
            if (firestoreRecipients == null || firestoreRecipients.Count == 0)
            {
                // If the recipients array is empty, initialize the list and add the new recipient
                u.Recipients = new List<string> { recipient };

                // Update the Firestore document with the new recipient
                await _usersRepository.AddUser(u, documentId);
            }
            else
            {
                // If the recipients array is not empty, use ArrayUnion to add the new recipient to Firestore
                await _usersRepository.UpdateUser(userId, documentId, recipient);
            }

            documentId = documentId + ".pdf";
            await _bucketsRepository.GrantAccessReader(documentId, recipient);

            TempData["message"] = "Shared Document";


            return View();
        }

        [Authorize]
        public async Task<IActionResult> Shared()
        {
            var sharedDocuments = await _documentsRepository.GetSharedDocuments(HttpContext);
            return View(sharedDocuments);
        }

        [Authorize]
        public async Task<IActionResult> Delete(string documentId)
        {
            await _documentsRepository.DeleteDocument(documentId);
            return RedirectToAction("Index");
        }

        public async Task<IActionResult> Generate(string documentId, string filename)
        {
            // Validate the input parameters
            if (string.IsNullOrEmpty(documentId))
            {
                return Content("Error: No document ID found.");
            }

            // Get the document file from the storage bucket using the documentId
            byte[] documentBytes = await _bucketsRepository.DownloadFile(documentId);

            // Validate that the document file is not null
            if (documentBytes == null)
            {
                return Content("Error: Document file not found.");
            }

            MemoryStream documentStream = new MemoryStream(documentBytes);
            Aspose.Words.Document doc = new Aspose.Words.Document(documentStream);

            // Save the document as PDF
            using (MemoryStream pdfStream = new MemoryStream())
            {
                doc.Save(pdfStream, SaveFormat.Pdf);
                await _bucketsRepository.UploadFile(filename, pdfStream);

                // Update the status of the document in the database
                await _documentsRepository.UpdateDocumentStatus(documentId);
            }
            // Redirect the user to download the PDF
            return Redirect("https://storage.cloud.google.com/msd-ha-librarybucket/" + filename);
        }


    [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
        public IActionResult Error()
        {
            return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
        }

        [Authorize] //access only allowed to this method to logged in users (i.e.: members)
        public IActionResult MembersLanding() { 
            return View(); 
        }


        [Authorize]
        public async Task<IActionResult> Logout()
        {
            await HttpContext.SignOutAsync(); //erases cookies which holds the user logged in
            return RedirectToAction("Index"); //redirects user to the Home Page

        }
    }
}