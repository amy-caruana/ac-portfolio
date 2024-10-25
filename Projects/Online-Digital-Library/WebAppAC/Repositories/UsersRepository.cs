using Google.Cloud.Firestore;
using Microsoft.CodeAnalysis;
using WebAppAC.Models;

namespace WebAppAC.Repositories
{
    public class UsersRepository
    {
        private FirestoreDb db;
        public UsersRepository(string project)
        {
            //initialising the database
            db = FirestoreDb.Create(project);
        }

        public async Task AddUser(User u, string documentId)
        {
            DocumentReference docRef = db.Collection("documents").Document(documentId).Collection("users").Document(u.Id);
            // Set the document data
            Dictionary<string, object> userData = new Dictionary<string, object>
            {
                {"DocumentId", documentId},
                {"Recipients", u.Recipients}
            };

            // Add the user document to the collection
            await docRef.SetAsync(userData);
        }

        public async Task UpdateUser(string userId, string documentId, string recipient)
        {
            DocumentReference docRef = db.Collection("documents").Document(documentId).Collection("users").Document(userId);

            // Use ArrayUnion to add the new recipient to the 'Recipients' array field
            Dictionary<string, object> updateData = new Dictionary<string, object>
            {
                {"Recipients", FieldValue.ArrayUnion(recipient)}
            };

            // Update the document with the new recipient
            await docRef.UpdateAsync(updateData);
        }

        public async Task<List<string>> GetRecipientsFromFirestore(string userId, string documentId)
        {
            DocumentReference docRef = db.Collection("documents").Document(documentId).Collection("users").Document(userId);

            // Retrieve the document snapshot from Firestore
            DocumentSnapshot snapshot = await docRef.GetSnapshotAsync();

            // Check if the document exists and contains the 'Recipients' field
            if (snapshot.Exists && snapshot.ContainsField("Recipients"))
            {
                // Retrieve the 'Recipients' array from the document
                List<string> recipients = snapshot.GetValue<List<string>>("Recipients");
                return recipients;
            }
            else
            {
                // If the document doesn't exist or doesn't contain the 'Recipients' field, return null
                return null;
            }
        }

        public async Task<List<User>> GetUsers(string documentId)
        {
            List<User> users = new List<User>(); //creating an empty list where it will be holding the returned user instances

            Query usersQuery = db.Collection($"documents/{documentId}/users"); //creating a query object to query the collection called documents
            QuerySnapshot usersQuerySnapshot = await usersQuery.GetSnapshotAsync();
            //looping with the snapshot because there might be more than 1 user
            foreach (DocumentSnapshot documentSnapshot in usersQuerySnapshot.Documents)
            {

                User u = documentSnapshot.ConvertTo<User>(); //converts from json data to a custom object
                u.Id = documentSnapshot.Id; //assign the id used for the document within the no-sql database
                u.DocumentId = documentId;
                users.Add(u); //adding the instance into the prepared list 
            }
            return users; //returning the prepared list
        }

        public async Task<User> GetUser(string documentId, string userId)
        {

            Query usersQuery = db.Collection($"documents/{documentId}/users/{userId}"); //creating a query object to query the collection called documents
            QuerySnapshot usersQuerySnapshot = await usersQuery.GetSnapshotAsync();

            if (usersQuerySnapshot.Documents.Count == 0)
            {
                return null;
            }
            DocumentSnapshot documentSnapshot = usersQuerySnapshot.Documents[0];
            
            User u = documentSnapshot.ConvertTo<User>(); //converts from json data to a custom object
            u.Id = documentSnapshot.Id; //assign the id used for the document within the no-sql database
            u.DocumentId = documentId;
            
            return u; //returning the prepared list
        }
    }
}
