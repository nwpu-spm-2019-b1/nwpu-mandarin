<template>
    <div style="height: 100%;" v-bind:style="{display: user !== null ? 'block' : 'none'}">
        <nav class="librarian-header" style="background-color: #e3f2fd;">
            <div class="container-fluid d-flex flex-row" style="justify-content: space-between; align-items: center;">
                <a href="/">
                    <img src="/static/images/logo.png" title="Mandarin" alt="Mandarin" class="navbar-logo"/>
                </a>
                <a href="javascript:void(0);" v-if="user!==null"><i class="fas fa-user mr-1"></i>{{user.username}}</a>
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
                            <li v-bind:class="{active: router.currentRoute.path==='/news'}">
                                <router-link to="/news">Manage news</router-link>
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
    import DashboardView from "./DashboardView.vue";
    import NewsView from "./NewsView.vue";
    import VueRouter from "vue-router";
    import {EventBus} from "../js/events.js";

    const routes = [
        {
            path: '/',
            component: DashboardView
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
        },
        {
            path: '/news',
            component: NewsView
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
                user: null
            };
        },
        computed: {
            router() {
                return router;
            }
        },
        beforeMount() {
            this.loadUserInfo();
        },
        methods: {
            onError(o) {
                showErrorToast(`<b>${o.error}</b>`);
            },
            loadUserInfo: async function () {
                let resp = await fetch("/api/librarian/user/current");
                let body = await resp.json();
                if (!resp.ok) {
                    alert(body.message);
                    return;
                }
                this.user = body.data;
                if (this.user === null) {
                    window.location = "/librarian/login";
                }
            }
        },
        components: {
            BookView,
            UserView,
            LendReturnView,
            BookEditor,
            IncomeHistoryView,
            CategoriesView,
            NewsView,
            Toast
        }
    };
</script>
<style>
</style>