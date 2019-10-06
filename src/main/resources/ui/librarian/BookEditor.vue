<template>
    <div>
        <h2>{{page_title}}</h2>
        <form @submit="submit">
            <div class="form-group">
                <label for="isbn-input">ISBN</label>
                <input class="form-control" id="isbn-input" name="isbn" v-model="book.isbn"/>
            </div>
            <div class="form-group">
                <label for="title-input">Title</label>
                <input class="form-control" id="title-input" name="title" v-model="book.title"/>
            </div>
            <div class="form-group">
                <label for="author-input">Author(s)</label>
                <input class="form-control" id="author-input" name="author" v-model="book.author"/>
            </div>
            <div class="form-group">
                <label for="description-input">Description</label>
                <textarea class="form-control" id="description-input" name="description" v-model="book.description">
                </textarea>
            </div>
            <div class="form-group">
                <label for="location-input">Location</label>
                <input class="form-control" id="location-input" name="location" v-model="book.location"/>
            </div>
            <div class="form-group">
                <label for="price-input">Price</label>
                <input class="form-control" id="price-input" name="price" v-model="book.price"/>
            </div>
            <div class="form-group" v-if="book_id===null">
                <label for="count-input">Number of books</label>
                <input type="number" min="0" class="form-control" id="count-input" name="count" v-model="book.count"/>
            </div>
            <div id="error" class="alert alert-danger" v-if="error!==null">{{error}}</div>
            <button type="submit" class="btn btn-success btn">{{submit_text}}</button>
            <button class="btn btn-secondary" @click="$router.back()">Go back</button>
        </form>
    </div>
</template>
<script>
    export default {
        name: "BookEditor",
        data: function () {
            return {
                book_id: this.$route.name !== "add-book" ? (this.$route.params.id) : null,
                book: {
                    isbn: '',
                    title: '',
                    author: '',
                    description: '',
                    location: '',
                    price: '',
                    category_ids: [],
                    count: 1
                },
                error: null,
                page_title: 'Editor',
                submit_text: 'Submit'
            };
        },
        mounted() {
            let vm = this;
            if (this.book_id !== null) {
                vm.page_title = "Edit book";
                vm.submit_text = "Save changes";
                $.ajax({
                    url: "/api/book/" + this.book_id,
                    type: "GET",
                    dataType: "json",
                    success: function (resp) {
                        Object.assign(vm.book, resp.data);
                    }
                });
            } else {
                vm.page_title = "Add books";
                vm.submit_text = "OK";
            }
        },
        methods: {
            submit(event) {
                event.preventDefault();
                let data = {
                    isbn: this.book.isbn,
                    title: this.book.title,
                    author: this.book.author,
                    description: this.book.description,
                    location: this.book.location,
                    price: this.book.price,
                    category_ids: []
                };
                console.log(data);
                let vm = this;
                let url, method;
                if (this.book_id !== null) {
                    url = "/api/librarian/book/" + this.book_id;
                    method = "PUT";
                } else {
                    url = "/api/librarian/book";
                    method = "POST";
                }
                $.ajax(
                    {
                        url: url,
                        type: method,
                        dataType: "json",
                        data: JSON.stringify(data),
                        contentType: 'application/json',
                        success: function (resp) {
                            console.log(vm.$router.history[vm.$router.history.length - 1]);
                            if (vm.$router.history.length >= 2) {
                                vm.$router.history.go(-1);
                            } else {
                                vm.$router.push({path: "/books"});
                            }
                        },
                        error: function (xhr) {
                            let resp = JSON.parse(xhr.responseText);
                            vm.error = resp.message;
                        }
                    }
                );
            }
        }
    }
</script>
<style scoped>
</style>