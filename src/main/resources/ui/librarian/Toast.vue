<template>
    <div role="alert" aria-live="assertive" aria-atomic="true" class="toast" data-autohide="false" id="error-toast">
        <div class="toast-header">
            <slot name="header">
                <!--<img src="..." class="rounded mr-2" alt="">-->
                <strong class="mr-auto">{{title}}</strong>
                <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close"
                        @click="$emit('toast-close')">
                    <span aria-hidden="true">&times;</span>
                </button>
            </slot>
        </div>
        <div class="toast-body">
            <slot></slot>
        </div>
    </div>
</template>
<script>
    export default {
        props: {
            title: String
        }
    }
</script>
<style>

</style>