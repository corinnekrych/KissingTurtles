// This wire spec exports the real game REST API which communicates with
// the monty hall server.
define({
	// Cretae and export an instance of the rest API, configured to point
	// to the correct host and to use a HATEOAS-aware rest implementation
	$exports: {
		create: 'app/games/restApi',
		properties: {
			gameClient: { $ref: 'gameClient' },
			host: { $ref: 'gameURL' },
            userIdNotification: { $ref: 'userIdNotification' }
		}
	},

	// A HATEOAS-aware rest implementation that understands how to parse JSON
	// entities out of the response body. It also looks for Location headers after
	// creating new resources using POST, and automatically GETs the newly created
	// resource.
	gameClient: {
		rest: [
			{ module: 'rest/interceptor/mime', config: { mime: 'application/json' } },
			{ module: 'rest/interceptor/hateoas', config: { target: '' } },
			{ module: 'rest/interceptor/location' },
			{ module: 'rest/interceptor/entity' }
		]
	},
    // Include the rest package's wire plugin, which provides the
	// nice "client!" reference resolver for easily creating rest client
	// instances
	plugins: [
		{ module: 'rest/wire' }
	]
});