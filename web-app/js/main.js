window.onload = function() {

	var editor = ace.edit("editor");
	editor.setTheme("ace/theme/clouds");
	editor.getSession().setMode("ace/mode/groovy");

	var commands = editor.commands;

	commands.addCommand({
		name : "save",
		bindKey : {
			win : "Ctrl-S",
			mac : "Command-S",
			sender : "editor"
		},
		exec : function() {
			var value = editor.getSession().getValue();
			var title = $('#titleCreate').val();			
			submitCreateForm(title, value, "#output");
		}
	});

    $('#runButton').bind('click',function() {
        var value = editor.getSession().getValue();
        var title = $('#titleCreate').val();
        submitCreateForm(title, value, "#output");
    });
}

function submitCreateForm(title, input, output) {
	var url = "http://localhost:8080/KissingTurtles/game/run?=";
	$.post(url, {
		title:"myScript", content:input
	},function (data) {
		// Get output for building path
		// $('#scriptContent').text(data.content);
	});
}


