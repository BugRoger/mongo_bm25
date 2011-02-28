function() {
  this.tf.forEach(
    function(z) {
      emit(z.t, { count : 1});
    }
  )
}    