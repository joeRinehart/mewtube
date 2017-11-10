databaseChangeLog = {

    changeSet(author: "joe (generated)", id: "1510255352179-1") {
        createTable(tableName: "application_role") {
            column(name: "id", type: "UUID") {
                constraints(nullable: "false")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "authority", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "joe (generated)", id: "1510255352179-2") {
        createTable(tableName: "application_user") {
            column(name: "id", type: "UUID") {
                constraints(nullable: "false")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "password_expired", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "username", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "account_locked", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "password_hash", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "account_expired", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "enabled", type: "BOOLEAN") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "joe (generated)", id: "1510255352179-3") {
        createTable(tableName: "application_user_application_role") {
            column(name: "application_user_id", type: "UUID") {
                constraints(nullable: "false")
            }

            column(name: "application_role_id", type: "UUID") {
                constraints(nullable: "false")
            }
        }
    }
    changeSet(author: "joe (generated)", id: "1510255352179-5") {
        addPrimaryKey(columnNames: "id", constraintName: "application_rolePK", tableName: "application_role")
    }

    changeSet(author: "joe (generated)", id: "1510255352179-6") {
        addPrimaryKey(columnNames: "id", constraintName: "application_userPK", tableName: "application_user")
    }

    changeSet(author: "joe (generated)", id: "1510255352179-7") {
        addPrimaryKey(columnNames: "application_user_id, application_role_id", constraintName: "application_user_application_rolePK", tableName: "application_user_application_role")
    }

    changeSet(author: "joe (generated)", id: "1510255352179-9") {
        addUniqueConstraint(columnNames: "authority", constraintName: "UC_APPLICATION_ROLEAUTHORITY_COL", tableName: "application_role")
    }

    changeSet(author: "joe (generated)", id: "1510255352179-10") {
        addUniqueConstraint(columnNames: "username", constraintName: "UC_APPLICATION_USERUSERNAME_COL", tableName: "application_user")
    }

    changeSet(author: "joe (generated)", id: "1510255352179-11") {
        addForeignKeyConstraint(baseColumnNames: "application_user_id", baseTableName: "application_user_application_role", constraintName: "FK9ql7shq947hh9um3po1eyvl2k", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "application_user")
    }

    changeSet(author: "joe (generated)", id: "1510255352179-13") {
        addForeignKeyConstraint(baseColumnNames: "application_role_id", baseTableName: "application_user_application_role", constraintName: "FKmxvkushvbxywinit6ljtsehpt", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "application_role")
    }
}
