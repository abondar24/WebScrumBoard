<template>
  <div>
    <div>
      <img src="@/assets/logo.png" alt="app-logo">
      <h1>{{ msg }}</h1>
    </div>
    <div>
      <b-button variant="primary" style="" v-on:click="redirectToRegister">Sign Up</b-button>
      <b-button variant="success" v-on:click="redirectToLogin">Log in</b-button>
    </div>

  </div>

</template>

<script>
    export default {
        name: 'Hello',
        data() {
            return {
                msg: 'Work With your projects in agile manner'
            }
        },
        methods: {
            redirectToLogin() {
                this.$router.push("login");
            },
            redirectToRegister() {
                this.$router.push("Register");
            }
        }
    }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  h1, h2 {
    font-weight: normal;
  }

  ul {
    list-style-type: none;
    padding: 0;
  }

  li {
    display: inline-block;
    margin: 0 10px;
  }

  a {
    color: #42b983;
  }
</style>
