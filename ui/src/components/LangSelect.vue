<template>
    <b-nav-item-dropdown v-bind:text="$t('lang')" >
        <b-dropdown-item href="#" @click="setLanguage('en')">English</b-dropdown-item>
        <b-dropdown-item href="#" @click="setLanguage('de')">Deutsch</b-dropdown-item>
        <b-dropdown-item href="#" @click="setLanguage('ru')">Русский</b-dropdown-item>
        <b-dropdown-item href="#" @click="setLanguage('es')">Español</b-dropdown-item>
        <b-dropdown-item href="#" @click="setLanguage('fr')">Français</b-dropdown-item>
    </b-nav-item-dropdown>
</template>

<script>
    import Vue from "vue";

    export default {
        name: "LangSelect",
        methods:{
            setLanguage(lang){
                Vue.i18n.set(lang);
            }
        },
    }
</script>

<style scoped>

</style>
