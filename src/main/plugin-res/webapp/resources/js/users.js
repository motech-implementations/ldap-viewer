$(document).ready( function () {

    var userApi = $('meta[name=userapi]').attr("content");
    var api = $('meta[name=api]').attr("content");

    var table = $('#user_table').DataTable({
        "processing": true,
        "serverSide": false,
        "ajax": {
            "url": userApi,
            "type": "POST",
            "contentType": "application/json",
            "data": function ( d ) {
                return JSON.stringify( d );
            }
        },
        columns: [
             {
                "className":      'details-control',
                "orderable":      false,
                "data":           '',
                "defaultContent": '<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>'
            },
            { "data": "username"},
            { "data": "name"},
            { "data": "email"}
        ]
    });

   $('#states_table').DataTable({
       "paging": false,
       "info": false
   });

   $('#districts_table').DataTable({
       "paging": false,
       "info": false
   });

   $('#user_table tbody').on('click', 'td.details-control', function () {
        var tr = $(this).closest('tr');
        var row = table.row( tr );

        window.location.href = 'ldap/user/' + row.data().username;
    });

    // replace districts on state change
    $("#state").change(function() {
        var state = $(this).val();
        $.get(api + '/districts/' + state, function(data) {
            var selectEl = $("#district");
            selectEl.empty(); // remove old options

            $.each(data.Item, function(index, value) {
              console.log("Found district: " + value.$);
              selectEl.append($("<option>", {
                value: value.$,
                text: value.$
              }));
            });
        });
    });
} );

function deleteUser(username) {
    var confirmation = confirm("Do you really want to delete this user? This operation is irreversible.")
    if (confirmation) {
        $.post(username + '/delete', function(data) {
            window.location.href = 'nms-users/ldap';
        });
    }
}