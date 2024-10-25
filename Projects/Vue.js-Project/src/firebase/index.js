// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getFirestore } from "firebase/firestore";

const firebaseConfig = {
  apiKey: "AIzaSyD_Vzc-Ft_bM9cCojsvgJEar8Awif6NAh8",
  authDomain: "bookstoreassigment.firebaseapp.com",
  projectId: "bookstoreassigment",
  storageBucket: "bookstoreassigment.appspot.com",
  messagingSenderId: "255485527508",
  appId: "1:255485527508:web:42cab2258d38a4cc3a8241",
  measurementId: "G-3PPVYSY37H"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Initialize Cloud Firestore and get a reference to the service
const db = getFirestore(app);
export{
  db
}