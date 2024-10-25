/*using Microsoft.AspNetCore.Mvc;
using WebAppAC.Repositories;
using PdfSharp.Fonts;
using Aspose.Words;
using Aspose.Pdf.Text;

public class ReportsController : Controller
{
    private PubSubRepository _pubsubRepository;
    private UsersRepository _usersRepository;
    private BucketsRepository _bucketsRepository;
    private DocumentsRepository _documentsRepository;

    public ReportsController(PubSubRepository pubsubRepository, UsersRepository usersRepository, BucketsRepository bucketsRepository,
        DocumentsRepository documentsRepository)
    {
        _pubsubRepository = pubsubRepository;
        _usersRepository = usersRepository;
        _bucketsRepository = bucketsRepository;
        _documentsRepository = documentsRepository;
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

        // Convert the document file bytes to a MemoryStream
        MemoryStream documentStream = new MemoryStream(documentBytes);

        // Load the DOCX document using Aspose.Words
        Aspose.Words.Document doc = new Aspose.Words.Document(documentStream);

        // Create a new PDF document using Aspose.PDF
        Aspose.Pdf.Document pdf = new Aspose.Pdf.Document();

        // Iterate through sections in the document
        foreach (Section section in doc.Sections)
        {
            // Create a new page in the PDF document
            Aspose.Pdf.Page pdfPage = pdf.Pages.Add();

            // Iterate through paragraphs in the section
            foreach (Paragraph para in section.Body.Paragraphs)
            {
                // Create a new text fragment for each paragraph
                TextFragment textFragment = new TextFragment();

                // Set the text and formatting of the text fragment
                textFragment.Text = para.GetText();
                textFragment.TextState.FontSize = 12; // Set font size

                // Add the text fragment to the PDF page
                pdfPage.Paragraphs.Add(textFragment);
            }
        }


        // Save the PDF document to a MemoryStream
        MemoryStream pdfStream = new MemoryStream();
        pdf.Save(pdfStream);

        // Upload the generated PDF to the storage bucket
        await _bucketsRepository.UploadFile(filename, pdfStream);
        
        // Redirect the user to download the PDF
        return Redirect("https://storage.cloud.google.com/msd-ha-librarybucket/" + filename);

    }


    public class FileFontResolver : IFontResolver // FontResolverBase
    {
        public string DefaultFontName => throw new NotImplementedException();

        public byte[] GetFont(string faceName)
        {
            using (var ms = new MemoryStream())
            {
                using (var fs = System.IO.File.Open(faceName, FileMode.Open))
                {
                    fs.CopyTo(ms);
                    ms.Position = 0;
                    return ms.ToArray();
                }
            }
        }

        public FontResolverInfo ResolveTypeface(string familyName, bool isBold, bool isItalic)
        {
            if (familyName.Equals("Verdana", StringComparison.CurrentCultureIgnoreCase))
            {
                if (isBold && isItalic)
                {
                    return new FontResolverInfo("Fonts/Verdana-BoldItalic.ttf");
                }
                else if (isBold)
                {
                    return new FontResolverInfo("Fonts/Verdana-Bold.ttf");
                }
                else if (isItalic)
                {
                    return new FontResolverInfo("Fonts/Verdana-Italic.ttf");
                }
                else
                {
                    return new FontResolverInfo("Fonts/Verdana.ttf");
                }
            }
            return null;
        }
    }
}*/