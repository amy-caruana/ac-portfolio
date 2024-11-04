import { createStore } from 'vuex'
import router from '../router'
import { auth } from '../firebase'

import { 
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
  signOut 
} from 'firebase/auth'

// Create and export the Vuex store
export default createStore({
  state: {
    user: null
  },

  // Getter to retrieve the user state
  getters:{
    GET_USER(state){
      return state.user;
    }
  },

  setters:{
    SET_USER(state){
      return state.user;
    }
  },
  
  mutations: {
    // Mutation to set the user state
    SET_USER (state, user) {
      state.user = user
    },

    // Mutation to clear the user state
    CLEAR_USER (state) {
      state.user = null
    }

  },

  actions: {
    // Action to handle user login
    async login ({ commit }, details) {
        const { email, password } = details;

        try {
            // Attempt to sign in with email and password
            await signInWithEmailAndPassword(auth, email, password);
            // If successful, commit the user to the store
            commit('SET_USER', auth.currentUser);
            // Redirect to the home page
            router.push('/');
        } catch (error) {
            // Log the error details and display an alert
            console.error('Error details:', error);
            alert(`Error Code: ${error.code} - ${error.message}`);
        }
    },



    // Action to handle user registration
    async register ({ commit}, details) {
       const { email, phone, password } = details

      try {
        await createUserWithEmailAndPassword(auth, email, password)
      } catch (error) {
        switch(error.code) {
          case 'auth/email-already-in-use':
            alert("Email already in use")
            break
          case 'auth/invalid-email':
            alert("Invalid email")
            break
          case 'auth/operation-not-allowed':
            alert("Operation not allowed")
            break
          case 'auth/weak-password':
            alert("Weak password")
            break
          default:
            alert("Something went wrong")
        }

        return
      }

      commit('SET_USER', auth.currentUser)

      router.push('/')
    },

    // Action to handle user logout
    async logout ({ commit }) {
      await signOut(auth)

      commit('CLEAR_USER')

      router.push('/login')
    },

    // Action to fetch user data
    fetchUser ({ commit }) {
      auth.onAuthStateChanged(async user => {
        if (user === null) {
          commit('CLEAR_USER')
        } else {
          commit('SET_USER', user)

          if (router.isReady() && router.currentRoute.value.path === '/login') {
            router.push('/')
          }
        }
      })
    }
  }
  
})
