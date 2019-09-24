<template>
  <div>
    <div>
      <img src="@/assets/logo.png" alt="app-logo">
      <h1>{{ msg }}</h1>
    </div>
    <div>
      <router-link to="/register">
        <b-button variant="primary" id="register">Sign Up</b-button>
      </router-link>

      <router-link to="/login">
        <b-button variant="success" id="login">Log in</b-button>
      </router-link>

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
      beforeMount(){
          if (this.authenticated){
            this.$router.push({path: '/user/' + this.getId});
          }
      },
      computed: {
        authenticated(){
          return this.$store.getters.getAuth;
        },
        getId() {
          return this.$store.getters.getUserId;
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

  #login {
    margin-left: 10px;
  }
</style>
