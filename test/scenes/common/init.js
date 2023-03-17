db.createUser({
	user: "holoinsight",
	pwd: "holoinsight",
	roles: [
		{role: "readWrite", db: "holoinsight"}
	]
})