<template>
    <div id="root">
        <div id="heading">
            <b-navbar type="dark" variant="dark">
                <b-navbar-brand href="#">Registration</b-navbar-brand>
                <b-navbar-nav class="ml-auto">
                    <b-nav-item right to="">
                        Log in
                    </b-nav-item>
                </b-navbar-nav>
            </b-navbar>
        </div>

        <div class="row">
            <div class="col-6 col-md-4" id="regForm">
               <RegisterForm/>
            </div>
        </div>
    </div>

</template>

<script>

    import RegisterForm from "./RegisterForm";

    export default {
        name: "Register",
        components: {RegisterForm}
    }
</script>

<style scoped>
    #root {
        text-align: left;
    }

    #regForm{
        margin-left: 20px;
        margin-top: 10px;
    }
</style>
