This code is written in C#. It uses the Google Cloud Storage API client libraries for .NET.
This webpage functions as an online digital library that allows users to upload, share, and access documents securely. Here’s how it works:

**1. User Login and Document Upload:**
```
	-After logging in, users can upload multiple Word documents (.docx) through their profile.
	-The files are stored in a Google Cloud bucket, allowing fine-grained permissions for future sharing.
	-A progress bar shows the upload status, and the uploaded file’s metadata (title, upload date, author, etc.) is saved in a NoSQL database.
```
**2. File Conversion and Permissions:**
```
	-Upon upload, a PubSub service initiates automatic conversion of each .docx file into a PDF.
	-Once converted, the PDF replaces the .docx file for integrity, with original Word files being deleted via a scheduled job.
	-The PDFs are stored in a cloud bucket with read permissions configured for the intended recipients.
```
**3. Access Control and Sharing:**
```
	-Users can list and download their own uploaded documents and shared documents.
	-Sharing settings are configurable so that users can specify who can view or download each document.
```
**4. Cloud Services and Security:**
```
	-The site leverages multiple cloud services, including Google Cloud Storage, PubSub for event handling, and Cloud Scheduler for document deletion.
	-Authentication is handled via OAuth 2.0, and data security is enhanced with permissions on each PDF and use of Secret Manager for sensitive keys.
```
**5. Logging and Caching:**
```
	-Logs for upload and conversion events are recorded for monitoring.
	-Frequently accessed menus, such as “My Artefacts” and “Shared With Me Artefacts,” are cached for quick loading.
```
### Online link:
https://webappac-msd63a-dfcmjjz5ja-uc.a.run.app/
