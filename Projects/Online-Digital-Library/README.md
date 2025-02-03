
# Digital Library Web Application

Welcome to the **Digital Library Web Application**! This platform enables users to upload, share, and securely access documents. Built using **C#** and integrated with **Google Cloud**, it offers a seamless experience for document management, conversion, and secure sharing.

---

## 🌟 Features

### **1. User Login & Document Upload**
- **User Authentication**: Secure login for users to manage their personal documents.
- **File Upload**: Upload multiple **Word documents** (.docx) via user profiles.
- **Cloud Storage**: Documents are stored securely in **Google Cloud Storage**, ensuring accessibility and control.
- **Progress Tracking**: Uploads are accompanied by a progress bar, and the metadata (title, upload date, author, etc.) is stored in a **NoSQL database**.

### **2. File Conversion & Permissions**
- **Automatic Conversion**: After upload, each **.docx** file is automatically converted into a **PDF** using Google Cloud Pub/Sub.
- **File Integrity**: The converted PDF replaces the original Word file to maintain consistency, with automatic deletion of the Word file through a scheduled task.
- **Access Control**: PDFs are stored in the cloud with configured **read permissions** for specific users.

### **3. Access Control & Sharing**
- **Document Management**: Users can list and download both their uploaded and shared documents.
- **Custom Permissions**: Flexible sharing settings allow users to specify who can view or download their documents.

### **4. Cloud Services & Security**
- **Cloud Integration**: Utilizes **Google Cloud Storage**, **Pub/Sub**, **Cloud Scheduler**, and **Secret Manager** for seamless file management and security.
- **OAuth 2.0 Authentication**: Ensures secure user login.
- **Sensitive Data Protection**: Sensitive API keys are stored securely using Google **Secret Manager**, ensuring privacy.

### **5. Logging & Caching**
- **Event Logging**: Logs document uploads and conversion events for real-time monitoring.
- **Caching for Speed**: Frequently accessed menus (e.g., “My Artefacts”, “Shared With Me Artefacts”) are cached to ensure quick access and enhanced user experience.

---

## ⚙️ Technologies Used

- **Backend**: **C#**, Google Cloud Storage API client libraries for **.NET**
- **Cloud Services**: **Google Cloud Storage**, **Pub/Sub**, **Cloud Scheduler**, **Secret Manager**
- **Authentication**: **OAuth 2.0**
- **Database**: **NoSQL** (for metadata storage)

---

## 🚀 Installation

To get started with this project locally, follow these steps:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/amy-caruana/online-digital-library.git
   ```

2. **Install dependencies**:
   - Make sure all necessary dependencies are installed. You can use **NuGet** to manage the packages in **.NET**:
     ```bash
     nuget restore
     ```

3. **Set up Google Cloud**:
   - Create and configure your Google Cloud project.
   - Enable and configure the required services:
     - **Cloud Storage**: For storing documents.
     - **Pub/Sub**: For triggering document conversions.
     - **Cloud Scheduler**: For scheduled document deletions.
     - **Secret Manager**: For securely managing sensitive keys.

4. **Run the application**:
   - Run the application locally using Visual Studio, or deploy it to your preferred cloud platform (Google Cloud, Azure, etc.).

---

## 🌐 Access the Application

You can try the live web application by visiting the link below:

[🔗 **Digital Library Web App**](https://webappac-msd63a-dfcmjjz5ja-uc.a.run.app/)

---

## 🤝 Contributing

We welcome contributions from everyone! To contribute:
- **Fork the project**
- **Submit an issue** if you encounter any problems or have suggestions
- **Open a pull request** for any improvements

---

## 📬 Contact

For questions, feedback, or support, feel free to reach out to me at [amycaruana2000@gmail.com].
