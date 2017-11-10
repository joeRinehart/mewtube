databaseChangeLog = {

    changeSet(author: "joe (generated)", id: "1510280764716-1") {
        addColumn(tableName: "uploaded_video") {
            column(name: "transcoding_started", type: "boolean", defaultValue:"false") {
                constraints(nullable: "false")
            }
        }
    }
}
