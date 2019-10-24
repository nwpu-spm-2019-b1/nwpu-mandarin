<template>
    <div style="height: 100%;">
        <nav class="librarian-header" style="background-color: #e3f2fd;">
            <div class="container-fluid">
                <a href="/" class="navbar-brand">
                    <img src="/static/images/logo.png" title="Mandarin" alt="Mandarin" class="navbar-logo"/>
                </a>
            </div>
        </nav>
        <div class="container-fluid librarian-content">
            <div style="height: 100%;">
                <div class="sidebar">
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
                            <li v-bind:class="{active: router.currentRoute.path==='/income/history'}">
                                <router-link to="/income/history">View income history</router-link>
                            </li>
                            <li v-bind:class="{active: router.currentRoute.path==='/categories'}">
                                <router-link to="/categories">Manage categories</router-link>
                            </li>
                        </ul>
                    </nav>
                </div>
                <div class="main-column">
                    <div class="pt-3 pl-3 pr-3">
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
    import IncomeHistoryView from "./IncomeHistoryView.vue";
    import CategoriesView from "./CategoriesView.vue";
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
            name: 'user-history',
            path: '/user/:id/history',
            component: UserHistoryView
        },
        {
            path: '/income/history',
            component: IncomeHistoryView
        },
        {
            path: '/categories',
            component: CategoriesView
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
            return {};
        },
        computed: {
            router() {
                return router;
            }
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
            IncomeHistoryView,
            CategoriesView,
            Toast
        }
    };
</script>
<style>
</style>