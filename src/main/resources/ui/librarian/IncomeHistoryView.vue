<template>
    <div class="income-history">
        <h1 class="module-title">Income history</h1>
        <div>
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>User ID</th>
                    <th>Username</th>
                    <th>Action</th>
                    <th>Amount</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="item in list">
                    <td>{{item.user.id}}</td>
                    <td>{{item.user.username}}</td>
                    <td>{{item.type}}</td>
                    <td>¥{{item.amount}}</td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

<script>
    export default {
        data: function () {
            return {
                list: []
            };
        },
        mounted() {
            this.load();
        },
        methods: {
            load: async function () {
                let resp = await fetch("/api/librarian/income/history");
                let body = await resp.json();
                if (!resp.ok) {
                    alert(body.message);
                    return;
                }
                this.list = body.data;
            }
        }
    }
</script>
<style>
</style>