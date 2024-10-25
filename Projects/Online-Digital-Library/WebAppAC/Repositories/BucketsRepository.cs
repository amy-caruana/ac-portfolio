using Google.Apis.Storage.v1;
using Google.Apis.Storage.v1.Data;
using Google.Cloud.Storage.V1;

namespace WebAppAC.Repositories
{
    public class BucketsRepository
    {
        private PubSubRepository _pubsubRepository;
        private string _projectId;
        private string _bucketName;
        private readonly StorageClient _storageClient;

        public BucketsRepository(string projectId, string bucketName) { 
            _projectId = projectId;
            _bucketName = bucketName;
            _storageClient = StorageClient.Create();
        }

        public async Task<Google.Apis.Storage.v1.Data.Object> UploadFile(string filename, MemoryStream ms)
        {
            var storage = StorageClient.Create();
            // if you dont await for this line to complete, then it starts the upload but it does not wait for it to finish
            // meaning that you might never see the file in the bucket
            // returning the result of this tine meaning a process which keeps track of the file being uploaded

            return await storage.UploadObjectAsync(_bucketName, filename, "application/octet-stream", ms);
        }

        public async Task<Google.Apis.Storage.v1.Data.Object> GrantAccess(string filename, string recipient)
        {
            var storage = StorageClient.Create();
            var storageObject = storage.GetObject(_bucketName, filename, new GetObjectOptions
            {
                Projection = Projection.Full
            });

            storageObject.Acl.Add(new ObjectAccessControl
            {
                Bucket = _bucketName,
                Entity = $"user-{recipient}",
                Role = "OWNER",
            });
            Console.WriteLine($"Added user {recipient} as an owner on file {filename}.");
            return await storage.UpdateObjectAsync(storageObject);
        }

        public async Task<Google.Apis.Storage.v1.Data.Object> GrantAccessReader(string documentId, string recipient)
        {
            var storage = StorageClient.Create();
            var storageObject = storage.GetObject(_bucketName, documentId, new GetObjectOptions
            {
                Projection = Projection.Full
            });

            storageObject.Acl.Add(new ObjectAccessControl
            {
                Bucket = _bucketName,
                Entity = $"user-{recipient}",
                Role = "READER",
            });
            Console.WriteLine($"Added user {recipient} as an reader on file {documentId}.");
            return await storage.UpdateObjectAsync(storageObject);
        }

        public async Task<byte[]> DownloadFile(string filePath)
        {
            using (MemoryStream memoryStream = new MemoryStream())
            {
                filePath = filePath + ".docx";

                await _storageClient.DownloadObjectAsync(_bucketName, filePath, memoryStream);
                return memoryStream.ToArray();
            }
        }
    }
}
