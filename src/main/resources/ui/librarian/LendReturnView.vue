<template>
    <div>
        <h1 class="module-title">Lend / return a book</h1>
        <form @submit="submit">
            <div class="form-group form-row">
                <label for="action-select" class="col-2 col-xl-1">Type of action</label>
                <select class="form-control col col-xl" id="action-select" name="action" v-model="action">
                    <option value="lend">Lend</option>
                    <option value="return">Return</option>
                </select>
            </div>
            <div class="form-group form-row">
                <label for="book-id-input" class="col-2 col-xl-1">Book ID</label>
                <input class="form-control col col-xl" id="book-id-input" name="book-id" v-model="book_id"
                       autocomplete="off"/>
                <button @click="loadBook"
                        class="btn btn-primary btn-block col-1 col-xl-1"
                        style="margin-left: 0.3em;"
                        type="button">
                    View book detail
                </button>
            </div>
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
            <div class="form-group form-row">
                <label for="username-input" class="col-2 col-xl-1">Username</label>
                <input class="form-control col" id="username-input" name="username" v-model="username"
                       autocomplete="off"/>
            </div>
            <div id="error-message" class="alert alert-danger" v-if="error!==null">{{error}}</div>
            <div id="success-message" class="alert alert-success" v-if="ok">
                Action successfully performed.
                <a href="javascript:void(0);" @click="clear">Click here to do this again</a>.
            </div>
            <button type="submit" class="btn btn-success btn">OK</button>
            <button class="btn btn-secondary" @click="$router.back()">Go back</button>
        </form>
    </div>
</template>
<script>
    export default {
        name: "LendReturnView",
        data: function () {
            return {
                book_id: '',
                username: '',
                book: null,
                error: null,
                ok: false,
                action: 'lend'
            };
        },
        methods: {
            clear() {
                this.book_id = '';
                this.username = '';
                this.book = null;
                this.error = null;
                this.ok = false;
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
            submit(event) {
                event.preventDefault();
                let url = null;
                let vm = this;
                if (this.action === "lend") {
                    url = "/api/librarian/book/lend";
                } else {
                    url = "/api/librarian/book/return";
                }
                $.ajax(
                    {
                        url: url,
                        type: "POST",
                        dataType: "json",
                        data: {
                            username: vm.username,
                            bookId: vm.book_id
                        },
                        success(resp) {
                            console.log(vm.$router.history[vm.$router.history.length - 1]);
                            if (vm.$router.history.length >= 2) {
                                vm.$router.history.go(-1);
                            } else {
                                vm.$router.push({path: "/books"});
                            }
                        },
                        error(xhr) {
                            let resp = JSON.parse(xhr.responseText);
                            vm.error = resp.message;
                        }
                    }
                );
            }
        }
    }
</script>
<style>
</style>