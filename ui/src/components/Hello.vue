<template>
  <div>
    <div>
      <img src="@/assets/logo.png" alt="app-logo">
      <h1>{{ $t('greet') }}</h1>
    </div>
    <div>
      <router-link to="/register">
        <b-button variant="primary" id="register">{{ $t('register') }}</b-button>
      </router-link>

      <router-link to="/login">
        <b-button variant="success" id="login">{{ $t('log_in') }}</b-button>
      </router-link>
    </div>
    <div>
      <LangSelect></LangSelect>
    </div>
  </div>

</template>

<script>
  import LangSelect from "./LangSelect";
    export default {
        name: 'Hello',
      components: {LangSelect},
      data() {
            return {
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
