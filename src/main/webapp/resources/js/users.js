$(document).ready( function () {
    var table = $('#user_table').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": "ldap/users",
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
            { "data": "email"},
            { "data": "admin"},
            { "data": "state"},
            { "data": "district"}
        ]
    });

   $('#user_table tbody').on('click', 'td.details-control', function () {
        var tr = $(this).closest('tr');
        var row = table.row( tr );

        window.location.href = 'ldap/user/' + row.data().username;
    });

    // replace districts on state change
    $("#state").change(function() {
        var state = $(this).val();
        $.get('districts/' + state, function(data) {
            var selectEl = $("#district");
            selectEl.empty(); // remove old options

            $.each(data, function(index, value) {
              console.log(value);
              selectEl.append($("<option></option>")
                 .attr("value", value).text(value));
            });

            selectEl.val("ALL");
        });
    });
} );

