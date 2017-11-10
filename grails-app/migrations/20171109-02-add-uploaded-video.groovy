databaseChangeLog = {

    changeSet(author: "joe (generated)", id: "1510256670806-1") {
        createTable(tableName: "uploaded_video") {
            column(name: "id", type: "UUID") {
                constraints(nullable: "false")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "transcoded", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "user_id", type: "UUID") {
                constraints(nullable: "false")
            }

            column(name: "uploaded", type: "BOOLEAN") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "joe (generated)", id: "1510256670806-2") {
        addPrimaryKey(columnNames: "id", constraintName: "uploaded_videoPK", tableName: "uploaded_video")
    }

    changeSet(author: "joe (generated)", id: "1510256670806-3") {
        addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "uploaded_video", constraintName: "FKlf5mui3aas4hd7genr828il8h", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "application_user")
    }
}
