databaseChangeLog = {

    changeSet(author: "joe (generated)", id: "1510317389459-1") {
        addColumn(tableName: "uploaded_video") {
            column(name: "duration", type: "varchar(255)") {
                constraints(nullable: "true")
            }
        }
    }

    changeSet(author: "joe (generated)", id: "1510317389459-2") {
        addColumn(tableName: "uploaded_video") {
            column(name: "resolution", type: "varchar(255)") {
                constraints(nullable: "true")
            }
        }
    }

    changeSet(author: "joe (generated)", id: "1510317389459-3") {
        addColumn(tableName: "uploaded_video") {
            column(name: "transcoding_job_id", type: "varchar(255)") {
                constraints(nullable: "true")
            }
        }
    }

}
