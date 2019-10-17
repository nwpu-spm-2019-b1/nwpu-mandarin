<template>
    <div style="height: 100%;">
        <nav class="navbar navbar-light pt-1 pb-1" style="background-color: #e3f2fd;">
            <div class="container-fluid pl-0 pr-0">
                <a href="/" class="navbar-brand">
                    <img src="/static/images/logo.png" title="Mandarin" alt="Mandarin" class="navbar-logo"/>
                </a>
            </div>
        </nav>
        <div class="container-fluid">
            <div class="row" style="height: 100%; flex-wrap: nowrap; width: 100vw;">
                <div class="col-2 col-xl-1 sidebar">
                    <nav class="sidebar-nav">
                        <ul>
                            <li v-bind:class="{active : router.currentRoute.path === '/'}">
                                <router-link to="/">Dashboard</router-link>
                            </li>
                            <li v-bind:class="{active : router.currentRoute.path === '/books'}">
                                <router-link to="/books">Manage books</router-link>
                            </li>
                            <li v-bind:class="{active : router.currentRoute.path === '/books/actions'}">
                                <router-link to="/books/actions">Lend / return books</router-link>
                            </li>
                            <li v-bind:class="{active : router.currentRoute.path === '/users'}">
                                <router-link to="/users">Manage users</router-link>
                            </li>
                        </ul>
                    </nav>
                </div>
                <div class="col col-xl main-column" style="flex-grow: 1;">
                    <div class="p-3">
                        <router-view></router-view>
                    </div>
                    <div class="footer">
                        Mandarin Library Automation<br>
                        NWPU SPM 2019<br>
                        Group B1
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
    import Toast from "./Toast.vue";
    import BookView from "./BookView.vue";
    import UserView from "./UserView.vue";
    import BookEditor from "./BookEditor.vue";
    import LendReturnView from "./LendReturnView.vue";
    import UserHistoryView from "./UserHistoryView.vue";
    import VueRouter from "vue-router";
    import {EventBus} from "../js/events.js";

    const routes = [
        {
            path: '/',
            component: {
                template: `<h1 class="module-title">Dashboard</h1>`
            }
        },
        {
            path: '/books',
            component: BookView
        },
        {
            name: 'add-book',
            path: '/books/add',
            component: BookEditor
        },
        {
            name: 'edit-book',
            path: '/books/edit/:id',
            component: BookEditor
        },
        {
            name: "lend-and-return",
            path: "/books/actions",
            component: LendReturnView
        },
        {
            path: '/users',
            component: UserView
        },
        {
            path: '/user/:id/history',
            component: UserHistoryView
        }
    ];

    const router = new VueRouter({
        mode: 'history',
        base: '/librarian/',
        routes
    });

    export default {
        router,
        data: function () {
            return {
                router
            };
        },
        mounted() {
            EventBus.$on("error", this.onError);
        },
        beforeDestroy() {
            EventBus.$off("error", this.onError);
        },
        methods: {
            onError(o) {
                showErrorToast(`<b>${o.error}</b>`);
            }
        },
        components: {
            BookView,
            UserView,
            LendReturnView,
            BookEditor,
            Toast
        }
    };
</script>
<style>
</style>