import { initializeApp } from "firebase/app";
import { getFirestore } from "firebase/firestore";
import { getAuth } from "firebase/auth";

const firebaseConfig = {
  apiKey: "",
  authDomain: "bookstoreassigment.firebaseapp.com",
  projectId: "bookstoreassigment",
  storageBucket: "bookstoreassigment.appspot.com",
  messagingSenderId: "",
  appId: "",
  measurementId: ""
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
export { auth }

// Initialize Cloud Firestore and get a reference to the service
const db = getFirestore(app);
export { db };
