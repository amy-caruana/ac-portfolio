using Google.Apis.Storage.v1;
using Google.Cloud.Firestore;
using Microsoft.CodeAnalysis;
using System.Security.Claims;
using WebAppAC.Models;
using Microsoft.AspNetCore.Http;
using Document = WebAppAC.Models.Document;

namespace WebAppAC.Repositories
{
    public class DocumentsRepository
    {
        private FirestoreDb db;
        public DocumentsRepository(string project) {

            //initialising the database
            db = FirestoreDb.Create(project);
        }
        public async void AddDocument(Document d)
        {
            DocumentReference docRef = db.Collection("documents").Document(d.Id);
            await docRef.SetAsync(d);
        }

        public async Task<WriteResult> DeleteDocument(string id)
        {
            DocumentReference docRef = db.Collection("documents").Document(id);
            return await docRef.DeleteAsync();
        }
        public async Task<List<Document>> GetDocuments(HttpContext httpContext)
        {
            List<Document> userDocuments = new List<Document>();

            // Retrieve currently authenticated user's email
            string userEmail = GetUserEmail(httpContext); // Implement this method to retrieve the user's email from your authentication mechanism

            Query documentsQuery = db.Collection("documents").WhereEqualTo("Recipient", userEmail);
            QuerySnapshot documentsQuerySnapshot = await documentsQuery.GetSnapshotAsync();

            foreach (DocumentSnapshot documentSnapshot in documentsQuerySnapshot.Documents)
            {
                Document document = documentSnapshot.ConvertTo<Document>();
                document.Id = documentSnapshot.Id;
                userDocuments.Add(document);
            }

            return userDocuments;
        }

        public async Task<List<Document>> GetSharedDocuments(HttpContext httpContext)
        {
            List<Document> sharedDocuments = new List<Document>();

            // Retrieve currently authenticated user's email
            string userEmail = GetUserEmail(httpContext); // Implement this method to retrieve the user's email from your authentication mechanism

            Query documentsQuery = db.Collection("documents");
            QuerySnapshot documentsQuerySnapshot = await documentsQuery.GetSnapshotAsync();

            foreach (DocumentSnapshot documentSnapshot in documentsQuerySnapshot.Documents)
            {
                Document document = documentSnapshot.ConvertTo<Document>();
                document.Id = documentSnapshot.Id;

                // Get the users subcollection of the current document
                CollectionReference usersCollection = documentSnapshot.Reference.Collection("users");
                Query usersQuery = usersCollection.WhereArrayContains("Recipients", userEmail);
                QuerySnapshot usersQuerySnapshot = await usersQuery.GetSnapshotAsync();

                // If the user's email exists in the users subcollection, consider the document as shared
                if (usersQuerySnapshot.Count > 0)
                {
                    sharedDocuments.Add(document);
                }
            }

            return sharedDocuments;
        }

        public string GetUserEmail(HttpContext httpContext)
        {
            // Retrieve the current user's email claim from HttpContext.User
            if (httpContext.User.Identity.IsAuthenticated)
            {
                var userEmailClaim = httpContext.User.FindFirst(ClaimTypes.Email);
                if (userEmailClaim != null)
                {
                    return userEmailClaim.Value;
                }
            }

            // Handle the case when no user is authenticated or when email claim is not found
            return null;
        }

        public async Task<Document> GetDocumentById(string id)
        {
            DocumentReference docRef = db.Collection("documents").Document(id);
            DocumentSnapshot snapshot = await docRef.GetSnapshotAsync();

            if (snapshot.Exists)
            {
                Document document = snapshot.ConvertTo<Document>();
                document.Id = snapshot.Id;
                return document;
            }
            else
            {
                return null;
            }
        }

        public async Task UpdateDocumentStatus(string documentId)
        {
            DocumentReference docRef = db.Collection("documents").Document(documentId);
            Dictionary<string, object> updates = new Dictionary<string, object>
            {
                { "Status", 1 },
            };

            await docRef.UpdateAsync(updates);
        }

/*        public async Task<bool> CheckPdfAvailability(string documentId)
        {
            // Construct the document ID with the ".pdf" extension
            string pdfDocumentId = documentId + ".pdf";

            return true;
        }*/

    }
}
