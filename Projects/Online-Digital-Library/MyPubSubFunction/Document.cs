using Google.Cloud.Firestore;

namespace MyPubSubFunction
{
    [FirestoreData]
    public class Document
    {
        public string Id { get; set; }

        [FirestoreProperty]
        public string Title { get; set; }
        [FirestoreProperty]
        public Timestamp DateUploaded { get; set; }
        [FirestoreProperty]
        public string Author { get; set; }

        [FirestoreProperty]
        public int Status { get; set; } // 0 = Pending, 1 = Converted

        [FirestoreProperty]
        public string File { get; set; }

        // [FirestoreProperty]
        // public string PDF { get; set; }

        // [FirestoreProperty]
        // public string Recipient { get; set; }

        // [FirestoreProperty]
        // public string UserId { get; set; }


    }
}
