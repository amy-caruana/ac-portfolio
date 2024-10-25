using CloudNative.CloudEvents;
using Google.Cloud.Functions.Framework;
using Google.Events.Protobuf.Cloud.PubSub.V1;
using Google.Cloud.Storage.V1;
using Google.Cloud.Firestore;
using Google.Apis.Storage.v1.Data;
using System;
using System.IO;
using System.Threading;
using System.Threading.Tasks;
using Aspose.Words;

namespace MyPubSubFunction
{
    public class Function : ICloudEventFunction<MessagePublishedData>
    {
        private readonly string _bucketName = "msd-ha-librarybucket";

        public async Task HandleAsync(CloudEvent cloudEvent, MessagePublishedData data, CancellationToken cancellationToken)
        {
            
            var documentId = data.Message?.TextData;
            Console.WriteLine($"Document ID is: {documentId}");

            var recipient = data.Message.Attributes["recipient"];
            Console.WriteLine($"The User's Email is: {recipient}");

            if (string.IsNullOrEmpty(documentId))
            {
                Console.WriteLine("Error: No document ID found in Pub/Sub message.");
                return;
            }
            try
            {
                // Validate the input parameters
                if (string.IsNullOrEmpty(documentId))
                {
                    Console.WriteLine($"Error: Failed to fetch document with ID {documentId}");
                }

                // Get the document file from the storage bucket using the documentId
                byte[] documentBytes = await DownloadFile(documentId);

                // Validate that the document file is not null
                if (documentBytes == null)
                {
                    Console.WriteLine($"Error: Failed to fetch document with ID {documentId} from storage.");
                }


                Console.WriteLine($"STARTING THE CONVERSION");

                using (MemoryStream documentStream = new MemoryStream(documentBytes))
                {
                    Aspose.Words.Document doc = new Aspose.Words.Document(documentStream);

                    using (MemoryStream pdfStream = new MemoryStream())
                    {
                        doc.Save(pdfStream, SaveFormat.Pdf);
                        Console.WriteLine("Saving Document as pdf");

                        pdfStream.Position = 0;

                        string filename = documentId + ".pdf";
                        Console.WriteLine($"File name here: {filename}");
                        await UploadFile(filename, pdfStream);
                        await GrantAccess(filename, recipient);

                        //status
                        var db = FirestoreDb.Create("acmsd-ha");
                        var documentRef = db.Collection("documents").Document(documentId);

                        var documentSnapshot = await documentRef.GetSnapshotAsync();
                        if (documentSnapshot.Exists)
                        {
                            await documentRef.UpdateAsync("Status", 1);
                            Console.WriteLine("Document status changed");
                        }

                        Console.WriteLine("Document converted successfully");
                    }
                }
                return;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error during conversion: {ex.Message}");
                return;
            }
        }

        public async Task<byte[]> DownloadFile(string filePath)
        {
            using (MemoryStream memoryStream = new MemoryStream())
            {
                filePath = filePath + ".docx";
                var storage = StorageClient.Create();

                Console.WriteLine($"Bucket Name: {_bucketName}");
                Console.WriteLine($"File Path: {filePath}");

                try
                {
                    await storage.DownloadObjectAsync(_bucketName, filePath, memoryStream);
                }
                catch (Google.GoogleApiException ex)
                {
                    // Log the exception details
                    Console.WriteLine($"Google API Exception: {ex.Message}");
                }
                catch (Exception ex)
                {
                    // Log any other exceptions
                    Console.WriteLine($"Error downloading file: {ex.Message}");
                }   

                //await _storageClient.DownloadObjectAsync(_bucketName, filePath, memoryStream);
                return memoryStream.ToArray();
            }
        }

        public async Task<Google.Apis.Storage.v1.Data.Object> UploadFile(string filename, MemoryStream ms)
        {
            var storage = StorageClient.Create();

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
    }
}