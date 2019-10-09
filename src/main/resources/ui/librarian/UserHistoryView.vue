<template>
    <div class="user-history-view">
        <h1 class="module-title">User history</h1>
        <h2>Reservations</h2>
        <table class="table table-striped">
            <thead>
            <tr>
                <th>#</th>
                <th>Book ID</th>
                <th>Book title</th>
                <th>Reserved at</th>
                <th>Expire at</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tr v-for="row in reservations">
                <td>{{row.id}}</td>
                <td>{{row.book.id}}</td>
                <td>{{row.book.title}}</td>
                <td>{{row.time}}</td>
                <td>{{row.deadline}}</td>
                <td></td>
            </tr>
        </table>
        <h2>Borrowing history</h2>
        <table class="table table-striped">
            <thead>
            <tr>
                <th>#</th>
                <th>Book ID</th>
                <th>Book title</th>
                <th>Start time</th>
                <th>End time</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tr v-for="row in history">
                <td>{{row.id}}</td>
                <td>{{row.book.id}}</td>
                <td>{{row.book.title}}</td>
                <td>{{row.startTime}}</td>
                <td>{{row.endTime}}</td>
                <td></td>
            </tr>
        </table>
    </div>
</template>
<script>
    import {urlWithParams} from '../js/common.js';

    export default {
        data() {
            return {
                reservations: [],
                history: [],
                user_id: null,
            };
        },
        mounted() {
            this.user_id = this.$route.params.id;
            this.loadHistory();
        },
        methods: {
            loadHistory() {
                fetch(urlWithParams("/api/librarian/user/reservations", {id: this.user_id}), {
                    method: "GET",
                    credentials: "same-origin"
                }).then(resp => resp.json()).then(resp => {
                    this.reservations = resp.data;
                }).catch(err => {
                    alert(err);
                });
                fetch(urlWithParams("/api/librarian/user/history", {id: this.user_id}), {
                    method: "GET",
                    credentials: "same-origin"
                }).then(resp => resp.json()).then(resp => {
                    this.history = resp.data;
                }).catch(err => {
                    alert(err);
                });
            }
        }
    }
</script>

<style>
</style>