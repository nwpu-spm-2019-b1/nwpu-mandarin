<template>
    <div class="modal fade" tabIndex="-1" role="dialog" v-bind:id="id">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">{{title}}</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true" v-html="'&times;'"></span>
                    </button>
                </div>
                <div class="modal-body">
                    <slot></slot>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary"
                            @click="(e)=>{e.preventDefault();$emit('modal-accept');}">
                        {{accept_text}}
                    </button>
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">
                        {{reject_text}}
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
    export default {
        props: ["id", "title", "on_accept", "accept_text", "reject_text"]
    }
</script>
<style>
</style>