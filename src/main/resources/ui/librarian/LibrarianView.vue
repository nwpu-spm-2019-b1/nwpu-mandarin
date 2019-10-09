<template>
    <div class="container-fluid">
        <div class="row">
            <div class="col-2 col-xl-1 sidebar">
                <nav class="sidebar-nav">
                    <ul>
                        <li v-bind:class="{active : router.currentRoute.path === '/'}">
                            <router-link to="/">Dashboard</router-link>
                        </li>
                        <li v-bind:class="{active : router.currentRoute.path === '/books'}">
                            <router-link to="/books">Manage books</router-link>
                        </li>
                        <li v-bind:class="{active : router.currentRoute.path === '/users'}">
                            <router-link to="/users">Manage users</router-link>
                        </li>
                    </ul>
                </nav>
            </div>
            <div class="col-10 col-xl-11 main-column">
                <div class="p-3">
                    <router-view></router-view>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
    import BookView from "./BookView.vue";
    import UserView from "./UserView.vue";
    import BookEditor from "./BookEditor.vue";
    import LendReturnView from "./LendReturnView.vue";
    import UserHistoryView from "./UserHistoryView.vue";
    import VueRouter from 'vue-router';

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
        components: {
            BookView,
            UserView,
            LendReturnView,
            BookEditor
        }
    };
</script>
<style>
</style>