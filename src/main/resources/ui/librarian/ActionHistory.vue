<template>
    <div class="income-history">
        <h1 class="module-title">Action history</h1>
        <div>
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>User ID</th>
                    <th>Username</th>
                    <th>Book title</th>
                    <th>Time</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="item in list">
                    <td>{{item.user.id}}</td>
                    <td>{{item.user.username}}</td>
                    <td>{{item.book.title}}</td>
                    <td>{{item.time}}</td>
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
            }
        },
        mounted() {
            this.load();
        },
        methods: {
            load: async function () {
                let resp = await fetch("/api/librarian/history/deletes");
                let body = await resp.json();
                if (!resp.ok) {
                    alert(body.message);
                    return;
                }
                this.list = body.data.map(item=>{
                    return {
                        user: item.user,
                        book: item.info.book,
                        time: item.time
                    };
                });
                console.log(this.list);
            }
        }
    }
</script>

<style>

</style>