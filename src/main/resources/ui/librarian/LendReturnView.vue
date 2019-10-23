<template>
    <div>
        <h1 class="module-title">Lend / return a book</h1>
        <form @submit="submit">
            <div class="form-group form-row">
                <label class="col-2 col-xl-1">Type of action</label>
                <div class="d-inline col col-xl">
                    <div class="form-check form-check-inline">
                        <input class="form-check-input" type="radio" name="action" value="lend" id="radio-action-lend"
                               v-model="action">
                        <label class="form-check-label" for="radio-action-lend">Lend</label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input" type="radio" name="action" value="return"
                               id="radio-action-return"
                               v-model="action">
                        <label class="form-check-label" for="radio-action-return">Return</label>
                    </div>
                </div>
            </div>
            <div class="form-group form-row" v-for="(item, index) in book_id_list">
                <label v-bind:for="'book-id-input-'+(index+1)" class="col-2 col-xl-1">Book ID {{index+1}}</label>
                <input class="form-control col col-xl" v-bind:id="'book-id-input-'+(index+1)" name="book-id"
                       v-model="item.value"
                       autocomplete="off"/>
                <!--
                <button @click="loadBook"
                        class="btn btn-primary btn-block col-2 col-xl-2"
                        style="margin-left: 0.3em; white-space: nowrap;"
                        type="button">
                    View book detail
                </button>
                -->
            </div>
            <!--
            <div v-if="book!==null">
                <table class="table table-striped">
                    <tr>
                        <td>ID</td>
                        <td>{{book.id}}</td>
                    </tr>
                    <tr>
                        <td>Title</td>
                        <td>{{book.title}}</td>
                    </tr>
                    <tr>
                        <td>Author</td>
                        <td>{{book.author}}</td>
                    </tr>
                    <tr>
                        <td>Description</td>
                        <td>{{book.description}}</td>
                    </tr>
                    <tr>
                        <td>Location</td>
                        <td>{{book.location}}</td>
                    </tr>
                    <tr>
                        <td>Price</td>
                        <td>{{book.price}}</td>
                    </tr>
                </table>
            </div>
            -->
            <div class="form-group form-row">
                <label for="userid-input" class="col-2 col-xl-1">User ID</label>
                <input class="form-control col" id="userid-input" name="userid" v-model="user_id"
                       autocomplete="off"/>
            </div>
            <div class="alert"
                 v-for="message in messages"
                 v-bind:class="message.success ? 'alert-success' : 'alert-danger'">
                Book #{{message.book_id}}:<br>
                {{message.message}}
            </div>
            <button type="submit" class="btn btn-success btn">OK</button>
            <button class="btn btn-secondary" @click="$router.back()">Go back</button>
        </form>
    </div>
</template>
<script>
    function data() {
        let data = {
            book_id_list: [],
            user_id: '',
            messages: [],
            ok: false
        };
        for (let i = 0; i < 3; i++) {
            data.book_id_list.push({value: ''});
        }
        return data;
    }

    export default {
        name: "LendReturnView",
        data: function () {
            return {
                ...data(),
                action: 'lend'
            };
        },
        methods: {
            clear() {
                Object.assign(this, data());
            },
            loadBook() {
                let vm = this;
                $.ajax({
                    url: "/api/book/" + this.book_id,
                    type: "GET",
                    dataType: "json",
                    success: function (resp) {
                        vm.book = resp.data;
                    }
                });
            },
            submit: async function (event) {
                event.preventDefault();
                let url = null;
                if (this.action === "lend") {
                    url = "/api/librarian/book/lend";
                } else {
                    url = "/api/librarian/book/return";
                }
                let resp = await fetch(url, {
                    method: "POST",
                    credentials: "same-origin",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        user_id: this.user_id,
                        book_id_list: this.book_id_list.map(item => item.value)
                    })
                });
                let body = await resp.json();
                if (resp.ok) {
                    this.messages = body.data;
                } else {
                    alert(body.message);
                }

            }
        }
    }
</script>
<style>
</style>