using Google.Cloud.Firestore;

namespace WebAppAC.Models
{
    [FirestoreData]
    public class User
    {
        public User()
        {
            Recipients = new List<string>();
        }
        public string Id { get; set; }

        [FirestoreProperty]
        public string Recipient { get; set; }

        [FirestoreProperty]
        public List<string> Recipients { get; set; }

        [FirestoreProperty]
        public string DocumentId { get; set; }

    }
}
