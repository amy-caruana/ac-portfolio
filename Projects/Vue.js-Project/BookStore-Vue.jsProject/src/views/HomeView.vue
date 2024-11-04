<template>
  <div class="home">
    <div class="title has-text-centred">View Books</div>
    <div class="form">
      
      <div
        v-for="todo in books"
        :class="{ 'has-background-success-light': todo.done }"
        :key="todo.id"
        class="card mb-5"
      >
        <div class="card-content">
          <div class="content">
            <div class="columns is-mobile is-vcentered">
              <div
                class="column"
                :class="{ 'has-text-success line-through': todo.done }"
              >
                {{ todo.bookName }}
              </div>
              <div class="column is-5 has-text-right">

              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="pagination">
        <button class="button is-normal prev-button" @click="previousPage" :disabled="currentPage === 1">&#60; Previous</button>
        <span>{{ currentPage }}/ {{ totalPages }}</span>
        <button class="button is-normal next-button" @click="nextPage" :disabled="currentPage === totalPages">Next &#62;</button>
      </div>
  </div>
</template>

<script>
//@ is an alias to our src folder
import { db } from "@/firebase";
//import { v4 as uuidv4 } from "uuid";

import {
  getFirestore,
  getDocs,
  querySnapshot,
  onSnapshot,
  collection,
  doc,
  deleteDoc,
  updateDoc,
  addDoc,
  orderBy,
  query,
  limit,
} from "firebase/firestore";

export default {
  name: "HomeView",
  components: {},

  
  data() {
    return {
      inputField: "",
      addedMessage: "",

      currentPage: 1,
      perPage: 1,

      books: [],
    };
  },
  computed: {
        totalPages() {
          return Math.ceil(this.books.length / this.perPage);
        },

        books() {
          const startIndex = (this.currentPage - 1) * this.perPage;
          const endIndex = startIndex + this.perPage;
          return this.books.slice(startIndex, endIndex);
        }
      },

  mounted() {
    //this will fire the first time the app is launched BUT also when
    //there is a change it will keep on listening and fire again
    onSnapshot(
      query(collection(db, "books"), orderBy("date", "desc")),
      (querySnapshot) => {
        const fbBooks = [];
        querySnapshot.forEach((doc) => {
          //console.log(doc.id, " => ", doc.data());
          const todo = {
            id: doc.id,
            bookName: doc.data().bookName,
            done: doc.data().done,
          };
          fbBooks.push(todo);
        });
        this.books = fbBooks;
      }
    );

    const booksCollectionQuery = query(
      collection(db, "books"),
      orderBy("date", "desc"),
      limit(3)
    );
  },

  methods: {
    addToDo() {
      addDoc(collection(db, "books"), {
        bookName: this.inputField,
        done: false,
        date: Date.now(),
      });
      this.inputField = "";
      this.addedMessage = "Task added";
      setTimeout(() => {
        this.addedMessage = "";
      }, 2000);
    },

    deleteToDo(id) {
      //this.books = this.books.filter((todo) => todo.id !== id);
      deleteDoc(doc(db, "books", id));
    },
    toggleDone(id) {
      const index = this.books.findIndex((todo) => todo.id == id);
      //this.books[index].done = !this.books[index].done;
      updateDoc(doc(db, "books", id), {
        done: !this.books[index].done,
      });
    },
    editButton(id){

    },

    nextPage() {
      console.log("testing NEXT");
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    },

    previousPage() {
      console.log("testing PREV");
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    }
  },
};
</script>
<style>
@import "bulma/css/bulma.min.css";

.form {
  max-width: 400px;
  padding: 20px;
  margin: 0 auto;
}

.line-through {
  text-decoration: line-through;
}

.green {
  color: green;
}
</style>
