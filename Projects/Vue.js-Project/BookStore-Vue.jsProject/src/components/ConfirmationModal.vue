<template>
    <div class="modal is-active">
      <div class="modal-background"></div>
      <div class="modal-content">
        <div class="box">
          <p>Are you sure you want to delete?</p>
          <transition name="slide-up">
            <div class="buttons">
              <button class="button is-danger" @click="confirm">Delete</button>
              <button class="button" @click="cancel">Cancel</button>
            </div>
          </transition>
        </div>
      </div>
    </div>
  </template>
  
<script>
  export default {
    methods: {
      confirm() {
        this.$emit('confirmed');
      },
      cancel() {
        this.$emit('cancelled');
      }
    }
  };
  </script>
  
  <style scoped>
  .modal {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .modal-background {
    background-color: rgba(0, 0, 0, 0.7);
  }
  
  .modal-content {
    max-width: 400px;
  }
  
  .box {
    background-color: #fff;
    padding: 20px;
  }
  
  .buttons {
    display: flex;
    justify-content: flex-end;
    margin-top: 10px;
  }


  /* SLIDE-UP TRANSITION */
  .slide-up-enter-from {
    transform: translateY(10px);
    opacity: 0;
  }

  .slide-up-enter-active {
    /* transition: opacity 1s ease-out; */
    transition: all 1s ease;
}
 </style>
  