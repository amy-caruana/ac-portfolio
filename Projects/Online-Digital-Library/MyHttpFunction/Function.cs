using Google.Cloud.Functions.Framework;
using Microsoft.AspNetCore.Http;
using System.Threading.Tasks;
using Google.Cloud.Storage.V1;
using System.Linq;

namespace MyHttpFunction;

public class Function : IHttpFunction
{

    public Function()
    {
          string pathToKeyFile = "";
          System.Environment.SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", pathToKeyFile);
    }

    public async Task HandleAsync(HttpContext context)
    {
      // Authenticate to Google Cloud Storage
            var storage = StorageClient.Create();

            // Define your bucket name
            string bucketName = "msd-ha-librarybucket";

            // List objects in the bucket
            var objects = storage.ListObjects(bucketName);

            // Iterate through the objects
            foreach (var obj in objects)
            {
                // Check if the object is a PDF file
                if (obj.Name.EndsWith(".pdf"))
                {
                    // Extract the document ID from the PDF filename
                    string documentId = obj.Name.Split('.').First();

                    // Construct the DOCX filename based on the document ID
                    string docxFileName = $"{documentId}.docx";

                    // Check if the DOCX file exists
                    var docxFile = objects.FirstOrDefault(o => o.Name == docxFileName);
                    if (docxFile != null)
                    {
                        // Delete the DOCX file
                        storage.DeleteObject(bucketName, docxFileName);
                    }
                }
            }

            // Return a response
            await context.Response.WriteAsync("DOCX files deleted successfully.");
        }
}