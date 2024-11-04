<template>
	<main class="login">
		<section class="forms">
			<!-- Register Form -->
			<form class="register" @submit.prevent="register">
				<h2>Register</h2>
				<div class="field">
					<input @blur="v$.register_form.email.$touch()" 
						class="input" 
						type="email" 
						v-model.trim="register_form.email"
						:class="{ 'is-danger': v$.register_form.email.$error }" 
						placeholder="Email"/>
					<div v-if="v$.register_form.email.$error">
						<div v-for="(error, index) in v$.register_form.email.$errors" :key="index">
							<p class="help is-danger">{{ error.$message }}</p>
						</div>
					</div>
				</div>
				<div class="field">
					<input @blur="v$.register_form.phone.$touch()" 
						class="input" 
						type="tel" 
						v-model.trim="register_form.phone"
						:class="{ 'is-danger': v$.register_form.phone.$error }" 
						placeholder="Mobile Number"/>
					<div v-if="v$.register_form.phone.$error">
						<div v-for="(error, index) in v$.register_form.phone.$errors" :key="index">
							<p class="help is-danger">{{ error.$message }}</p>
						</div>
					</div>
				</div>				
				<div class="field">
					<input @blur="v$.register_form.password.$touch()" 
						class="input" 
						type="password" 
						v-model.trim="register_form.password"
						:class="{ 'is-danger': v$.register_form.password.$error }" 
						placeholder="Password"/>
					<div v-if="v$.register_form.password.$error">
						<div v-for="(error, index) in v$.register_form.password.$errors" :key="index">
							<p class="help is-danger">{{ error.$message }}</p>
						</div>
					</div>
				</div>			
				<button :disabled="!v$.register_form.$pending && v$.register_form.$invalid" class="button is-info">Register</button>
			</form>

			<!-- Login Form -->
			<form class="login" @submit.prevent="login">
				<h2>Login</h2>
				<div class="field">
					<input @blur="v$.login_form.email.$touch()" 
						class="input" 
						type="email" 
						v-model.trim="login_form.email"
						:class="{ 'is-danger': v$.login_form.email.$error }" 
						placeholder="Email"/>
					<div v-if="v$.login_form.email.$error">
						<div v-for="(error, index) in v$.login_form.email.$errors" :key="index">
							<p class="help is-danger">{{ error.$message }}</p>
						</div>
					</div>
				</div>
				<div class="field">
					<input @blur="v$.login_form.password.$touch()" 
						class="input" 
						type="password" 
						v-model.trim="login_form.password"
						:class="{ 'is-danger': v$.login_form.password.$error }" 
						placeholder="Password"/>
					<div v-if="v$.login_form.password.$error">
						<div v-for="(error, index) in v$.login_form.password.$errors" :key="index">
							<p class="help is-danger">{{ error.$message }}</p>
						</div>
					</div>
				</div>
				<button :disabled="!v$.login_form.$pending && v$.login_form.$invalid" class="button is-info">Login</button>
			</form>
		</section>
	</main>
</template>

<script>
import { ref } from 'vue'
import { useStore } from 'vuex'
import useVuelidate from '@vuelidate/core'
import { required, minLength, maxLength, email, numeric } from '@vuelidate/validators'

export default {
	setup () {
		// Create reactive references for login and register form data
		const login_form = ref({ email: "", password: "" });
		const register_form = ref({ email: "", phone: "", password: "" });

		// Access the Vuex store
		const store = useStore();

		const login = () => {
			store.dispatch('login', login_form.value);
		}

		const register = () => {
			store.dispatch('register', register_form.value);
		}

		return {
			login_form,
			register_form,
			login,
			register,
			// Initialize vuelidate validation
			v$: useVuelidate()
		}
	},

	validations() {
		return {
			register_form: {
				email: { required, minLength: minLength(5), maxLength: maxLength(35), email },
				phone: { required, minLength: minLength(8), maxLength: maxLength(8), numeric },
				password: { required, minLength: minLength(3) },
			},
			login_form: {
				email: { required, minLength: minLength(5), maxLength: maxLength(35), email },
				password: { required, minLength: minLength(3) },
			}
		}
	}
}
</script>

<style> 

.forms {
	display: flex;
	min-height: 100vh;
}

form {
	flex: 1 1 0%;
	padding: 8rem 1rem 1rem;
}

form.register {
	color: #FFF;
	background-color: rgb(66, 147, 245);
	background-image: linear-gradient(
		to bottom right,
		rgb(66, 147, 245) 0%,
		rgb(35, 78, 138) 100%
	);
}

h2 {
	font-size: 2rem;
	text-transform: uppercase;
	margin-bottom: 2rem;
}

input {
	appearance: none;
	border: none;
	outline: none;
	background: none;

	display: block;
	width: 100%;
	max-width: 400px;
	margin: 0 auto;
	font-size: 1.5rem;
	margin-bottom: 2rem;
	padding: 0.5rem 0rem;
}

input:not([type="submit"]) {
	opacity: 0.8;
	transition: 0.4s;
}

input:focus:not([type="submit"]) {
	opacity: 1;
}

input::placeholder {
	color: inherit;
}

form.register input:not([type="submit"]) {
	color: #2c3e50;
	border-bottom: 2px solid #2c3e50;
}

form.login input:not([type="submit"]) {
	color: #2c3e50;
	border-bottom: 2px solid #2c3e50;
}

form.login input[type="submit"] {
	background-color: rgb(74, 88, 236);
	color: #FFF;
	font-weight: 700;
	padding: 1rem 2rem;
	border-radius: 0.5rem;
	cursor: pointer;
	text-transform: uppercase;
}

form.register input[type="submit"] {
	background-color: #FFF;
	color: rgb(36, 71, 224);
	font-weight: 700;
	padding: 1rem 2rem;
	border-radius: 0.5rem;
	cursor: pointer;
	text-transform: uppercase;
}

</style>