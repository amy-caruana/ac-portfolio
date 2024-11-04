<template>
  <div class="home">
    <div class="title has-text-centred">Add/Update Books</div>
    <div class="form">
      <form @submit.prevent="addToDo()">
        <div class="field is-grouped mb-5">
          <div class="control is-expanded">
            <input
              class="input"
              v-model="inputField"
              type="text"
              placeholder="Add book"
            />
          </div>
          <transition-group name="fade" tag="div">
          <div class="control">
            <button :disabled="!inputField" class="button is-info">Add</button>
            <div class="green">{{ addedMessage }}</div>
          </div>
        </transition-group>
        </div>
      </form>
      <div
        v-for="todo in books"
        :class="{ 'has-background-success-light': todo.done }"
        :key="todo.id"
        class="card mb-5"
      >
        <div class="card-content">
          <div class="content">
            <div class="columns is-mobile is-vcentered">
              <transition name="fade">
              <div
                class="column"
                :class="{ 'has-text-success line-through': todo.done }"
              >
                {{ todo.bookName }}
              </div>
              </transition>
              <div class="column is-5 has-text-right">
                <button
                  @click="toggleDone(todo.id)"
                  class="button"
                  :class="todo.done ? 'is-success' : 'is-light'"
                >
                  &check;
                </button>
                <transition name="fade">
                  <button
                    @click="deleteToDo(todo.id)"
                    class="button is-danger ml-2"
                  >
                    &cross;
                  </button>
                </transition>
                <button
                  @click="editButton(todo.id)"
                  class="button is-warning ml-5"
                >
                  Update
                </button>
                <ConfirmationModal
                  v-if="showConfirmationModal"
                  @confirmed="deleteItem"
                  @canceled="cancelDelete"
                ></ConfirmationModal>
                
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
import ConfirmationModal from '@/components/ConfirmationModal.vue';

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
  name: "Secret",
  components: {},
  ConfirmationModal,

  
  data() {
    return {
      showConfirmationModal: false,
      itemIdToDelete: null,
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
      this.addedMessage = "Book added";
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
      const index = this.books.findIndex((todo) => todo.id == id);
      //this.books[index].done = !this.books[index].done;
      updateDoc(doc(db, "books", id), {
        done: !this.books[index].done,
    });
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
    },

    confirmDelete(itemId) {
      this.showConfirmationModal = true;
      this.itemIdToDelete = itemId;
    },
    cancelDelete() {
      this.itemIdToDelete = null;
      this.showConfirmationModal = false;
    },
    deleteItem() {
      if (this.itemIdToDelete) {
        this.deleteToDo(this.itemIdToDelete);
        this.itemIdToDelete = null;
        this.showConfirmationModal = false;
      }
    },
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

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}
</style>
