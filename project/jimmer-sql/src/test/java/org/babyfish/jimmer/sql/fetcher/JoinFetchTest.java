package org.babyfish.jimmer.sql.fetcher;

import org.babyfish.jimmer.sql.JoinType;
import org.babyfish.jimmer.sql.ast.query.ConfigurableRootQuery;
import org.babyfish.jimmer.sql.common.AbstractQueryTest;
import org.babyfish.jimmer.sql.model.*;
import org.babyfish.jimmer.sql.model.flat.CityFetcher;
import org.babyfish.jimmer.sql.model.flat.CompanyTable;
import org.babyfish.jimmer.sql.model.flat.StreetFetcher;
import org.babyfish.jimmer.sql.model.inheritance.AdministratorFetcher;
import org.babyfish.jimmer.sql.model.inheritance.AdministratorMetadataFetcher;
import org.babyfish.jimmer.sql.model.inheritance.AdministratorTable;
import org.junit.jupiter.api.Test;

public class JoinFetchTest extends AbstractQueryTest {

    @Test
    public void testExplicitJoinFetch() {
        BookTable table = BookTable.$;
        executeAndExpect(
                getSqlClient()
                        .createQuery(table)
                        .where(table.name().eq("GraphQL in Action"))
                        .orderBy(table.edition())
                        .select(
                                table.fetch(
                                        BookFetcher.$
                                                .allScalarFields()
                                                .store(
                                                        ReferenceFetchType.JOIN_ALWAYS,
                                                        BookStoreFetcher.$
                                                                .allScalarFields()
                                                )
                                )
                        ),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.ID, tb_1_.NAME, tb_1_.EDITION, tb_1_.PRICE, " +
                                    "tb_2_.ID, tb_2_.NAME, tb_2_.WEBSITE, tb_2_.VERSION " +
                                    "from BOOK tb_1_ " +
                                    "left join BOOK_STORE tb_2_ on tb_1_.STORE_ID = tb_2_.ID " +
                                    "where tb_1_.NAME = ? " +
                                    "order by tb_1_.EDITION asc"
                    );
                    ctx.rows(
                            "[{" +
                                    "--->\"id\":\"a62f7aa3-9490-4612-98b5-98aae0e77120\"," +
                                    "--->\"name\":\"GraphQL in Action\"," +
                                    "--->\"edition\":1," +
                                    "--->\"price\":80.00," +
                                    "--->\"store\":{" +
                                    "--->--->\"id\":\"2fa3955e-3e83-49b9-902e-0465c109c779\"," +
                                    "--->--->\"name\":\"MANNING\"," +
                                    "--->--->\"website\":null," +
                                    "--->--->\"version\":0" +
                                    "--->}" +
                                    "},{" +
                                    "--->\"id\":\"e37a8344-73bb-4b23-ba76-82eac11f03e6\"," +
                                    "--->\"name\":\"GraphQL in Action\",\"edition\":2," +
                                    "--->\"price\":81.00," +
                                    "--->\"store\":{" +
                                    "--->--->\"id\":\"2fa3955e-3e83-49b9-902e-0465c109c779\"," +
                                    "--->--->\"name\":\"MANNING\"," +
                                    "--->--->\"website\":null," +
                                    "--->--->\"version\":0" +
                                    "--->}" +
                                    "},{" +
                                    "--->\"id\":\"780bdf07-05af-48bf-9be9-f8c65236fecc\"," +
                                    "--->\"name\":\"GraphQL in Action\"," +
                                    "--->\"edition\":3," +
                                    "--->\"price\":80.00," +
                                    "--->\"store\":{" +
                                    "--->--->\"id\":\"2fa3955e-3e83-49b9-902e-0465c109c779\"," +
                                    "--->--->\"name\":\"MANNING\"," +
                                    "--->--->\"website\":null," +
                                    "--->--->\"version\":0" +
                                    "--->}" +
                                    "}]"
                    );
                }
        );
    }

    @Test
    public void testImplicitJoinFetch() {
        BookTable table = BookTable.$;
        executeAndExpect(
                getSqlClient(it -> {
                    it.setDefaultReferenceFetchType(ReferenceFetchType.JOIN_ALWAYS);
                })
                        .createQuery(table)
                        .where(table.name().eq("GraphQL in Action"))
                        .orderBy(table.edition())
                        .select(
                                table.fetch(
                                        // Here, `allScalars` is not used
                                        // to test `select tb_1_...., tb_2_...., tb_1_.... from ... `
                                        BookFetcher.$
                                                .name()
                                                .store(
                                                        ReferenceFetchType.JOIN_ALWAYS,
                                                        BookStoreFetcher.$
                                                                .allScalarFields()
                                                )
                                                .edition()
                                                .price()
                                )
                        ),
                ctx -> {
                    ctx.sql(
                            "select " +
                                    "tb_1_.ID, tb_1_.NAME, " +
                                    "tb_2_.ID, tb_2_.NAME, tb_2_.WEBSITE, tb_2_.VERSION, " +
                                    "tb_1_.EDITION, tb_1_.PRICE " +
                                    "from BOOK tb_1_ " +
                                    "left join BOOK_STORE tb_2_ on tb_1_.STORE_ID = tb_2_.ID " +
                                    "where tb_1_.NAME = ? " +
                                    "order by tb_1_.EDITION asc"
                    );
                    ctx.rows(
                            "[{" +
                                    "--->\"id\":\"a62f7aa3-9490-4612-98b5-98aae0e77120\"," +
                                    "--->\"name\":\"GraphQL in Action\"," +
                                    "--->\"edition\":1," +
                                    "--->\"price\":80.00," +
                                    "--->\"store\":{" +
                                    "--->--->\"id\":\"2fa3955e-3e83-49b9-902e-0465c109c779\"," +
                                    "--->--->\"name\":\"MANNING\"," +
                                    "--->--->\"website\":null," +
                                    "--->--->\"version\":0" +
                                    "--->}" +
                                    "},{" +
                                    "--->\"id\":\"e37a8344-73bb-4b23-ba76-82eac11f03e6\"," +
                                    "--->\"name\":\"GraphQL in Action\",\"edition\":2," +
                                    "--->\"price\":81.00," +
                                    "--->\"store\":{" +
                                    "--->--->\"id\":\"2fa3955e-3e83-49b9-902e-0465c109c779\"," +
                                    "--->--->\"name\":\"MANNING\"," +
                                    "--->--->\"website\":null," +
                                    "--->--->\"version\":0" +
                                    "--->}" +
                                    "},{" +
                                    "--->\"id\":\"780bdf07-05af-48bf-9be9-f8c65236fecc\"," +
                                    "--->\"name\":\"GraphQL in Action\"," +
                                    "--->\"edition\":3," +
                                    "--->\"price\":80.00," +
                                    "--->\"store\":{" +
                                    "--->--->\"id\":\"2fa3955e-3e83-49b9-902e-0465c109c779\"," +
                                    "--->--->\"name\":\"MANNING\"," +
                                    "--->--->\"website\":null," +
                                    "--->--->\"version\":0" +
                                    "--->}" +
                                    "}]"
                    );
                }
        );
    }

    @Test
    public void testMergeJoinAndJoinFetch() {
        BookTable table = BookTable.$;
        executeAndExpect(
                getSqlClient()
                        .createQuery(table)
                        // (1) Joins will be merge in `and` predicate
                        .where(table.store().name().eq("MANNING"))
                        .where(table.store(JoinType.LEFT).website().isNull())
                        .orderBy(table.edition())
                        .select(
                                // (2) However, join fetch will not be merged with join
                                table.fetch(
                                        // Here, `allScalars` is not used
                                        // to test `select tb_1_...., tb_2_...., tb_1_.... from ... `
                                        BookFetcher.$
                                                .name()
                                                .store(
                                                        ReferenceFetchType.JOIN_ALWAYS,
                                                        BookStoreFetcher.$
                                                                .allScalarFields()
                                                )
                                                .edition()
                                                .price()
                                )
                        ),
                ctx -> {
                    ctx.sql(
                            "select " +
                                    "tb_1_.ID, tb_1_.NAME, " +
                                    "tb_2_.ID, tb_2_.NAME, tb_2_.WEBSITE, tb_2_.VERSION, " +
                                    "tb_1_.EDITION, tb_1_.PRICE " +
                                    "from BOOK tb_1_ " +
                                    "inner join BOOK_STORE tb_2_ on tb_1_.STORE_ID = tb_2_.ID " +
                                    "where tb_2_.NAME = ? and tb_2_.WEBSITE is null " +
                                    "order by tb_1_.EDITION asc"
                    );
                    ctx.rows(
                            "[{" +
                                    "--->\"id\":\"a62f7aa3-9490-4612-98b5-98aae0e77120\"," +
                                    "--->\"name\":\"GraphQL in Action\"," +
                                    "--->\"edition\":1," +
                                    "--->\"price\":80.00," +
                                    "--->\"store\":{" +
                                    "--->--->\"id\":\"2fa3955e-3e83-49b9-902e-0465c109c779\"," +
                                    "--->--->\"name\":\"MANNING\"," +
                                    "--->--->\"website\":null," +
                                    "--->--->\"version\":0" +
                                    "--->}" +
                                    "},{" +
                                    "--->\"id\":\"e37a8344-73bb-4b23-ba76-82eac11f03e6\"," +
                                    "--->\"name\":\"GraphQL in Action\",\"edition\":2," +
                                    "--->\"price\":81.00," +
                                    "--->\"store\":{" +
                                    "--->--->\"id\":\"2fa3955e-3e83-49b9-902e-0465c109c779\"," +
                                    "--->--->\"name\":\"MANNING\"," +
                                    "--->--->\"website\":null," +
                                    "--->--->\"version\":0" +
                                    "--->}" +
                                    "},{" +
                                    "--->\"id\":\"780bdf07-05af-48bf-9be9-f8c65236fecc\"," +
                                    "--->\"name\":\"GraphQL in Action\"," +
                                    "--->\"edition\":3," +
                                    "--->\"price\":80.00," +
                                    "--->\"store\":{" +
                                    "--->--->\"id\":\"2fa3955e-3e83-49b9-902e-0465c109c779\"," +
                                    "--->--->\"name\":\"MANNING\"," +
                                    "--->--->\"website\":null," +
                                    "--->--->\"version\":0" +
                                    "--->}" +
                                    "}]"
                    );
                }
        );
    }

    @Test
    public void testMaxJoinFetchDepth() {
        TreeNodeTable table = TreeNodeTable.$;
        executeAndExpect(
                getSqlClient(it -> it.setMaxJoinFetchDepth(2))
                        .createQuery(table)
                        .where(table.id().eq(24L))
                        .select(
                                table.fetch(
                                        TreeNodeFetcher.$
                                                .allScalarFields()
                                                .parent(
                                                        ReferenceFetchType.JOIN_ALWAYS,
                                                        TreeNodeFetcher.$
                                                                .allScalarFields()
                                                                .parent(
                                                                        ReferenceFetchType.JOIN_ALWAYS,
                                                                        TreeNodeFetcher.$
                                                                                .allScalarFields()
                                                                                .parent(
                                                                                        ReferenceFetchType.JOIN_ALWAYS,
                                                                                        TreeNodeFetcher.$
                                                                                                .allScalarFields()
                                                                                                .parent(
                                                                                                        ReferenceFetchType.JOIN_ALWAYS,
                                                                                                        TreeNodeFetcher.$
                                                                                                                .allScalarFields()
                                                                                                )
                                                                                )
                                                                )
                                                )
                                )
                        ),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME, " +
                                    "tb_2_.NODE_ID, tb_2_.NAME, " +
                                    "tb_3_.NODE_ID, tb_3_.NAME, tb_3_.PARENT_ID " + // Unfinished, select foreign key
                                    "from TREE_NODE tb_1_ " +
                                    "left join TREE_NODE tb_2_ on tb_1_.PARENT_ID = tb_2_.NODE_ID " +
                                    "left join TREE_NODE tb_3_ on tb_2_.PARENT_ID = tb_3_.NODE_ID " +
                                    "where tb_1_.NODE_ID = ?"
                    ).variables(24L);
                    ctx.statement(1).sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME, " +
                                    "tb_2_.NODE_ID, tb_2_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "left join TREE_NODE tb_2_ on tb_1_.PARENT_ID = tb_2_.NODE_ID " +
                                    "where tb_1_.NODE_ID = ?"
                    ).variables(9L);
                    ctx.row(
                            0,
                            "{" +
                                    "--->\"id\":24," +
                                    "--->\"name\":\"Shirt\"," +
                                    "--->\"parent\":{" +
                                    "--->--->\"id\":22," +
                                    "--->--->\"name\":\"Formal wear\"," +
                                    "--->--->\"parent\":{" +
                                    "--->--->--->\"id\":18," +
                                    "--->--->--->\"name\":\"Man\"," +
                                    "--->--->--->\"parent\":{" +
                                    "--->--->--->--->\"id\":9," +
                                    "--->--->--->--->\"name\":\"Clothing\"," +
                                    "--->--->--->--->\"parent\":{" +
                                    "--->--->--->--->--->\"id\":1," +
                                    "--->--->--->--->--->\"name\":\"Home\"" +
                                    "--->--->--->--->}" +
                                    "--->--->--->}" +
                                    "--->--->}" +
                                    "--->}" +
                                    "}"
                    );
                }
        );
    }

    @Test
    public void testPage() {
        // Data query uses fetch
        // Count query ignore fetch
        BookTable table = BookTable.$;
        ConfigurableRootQuery<BookTable, Book> query = getSqlClient()
                .createQuery(table)
                .where(table.name().ilike("graphql"))
                .orderBy(table.name(), table.edition())
                .select(
                        table.fetch(
                                BookFetcher.$
                                        .allScalarFields()
                                        .store(
                                                ReferenceFetchType.JOIN_ALWAYS,
                                                BookStoreFetcher.$
                                                        .name()
                                        )
                        )
                );
        for (int i = 0; i < 2; i++) {
            connectAndExpect(
                    con -> query.fetchPage(1, 2, con),
                    ctx -> {
                        ctx.sql(
                                "select count(1) from BOOK tb_1_ " +
                                        "where tb_1_.NAME ilike ?"
                        );
                        ctx.statement(1).sql(
                                "select tb_1_.ID, tb_1_.NAME, tb_1_.EDITION, tb_1_.PRICE, " +
                                        "tb_2_.ID, tb_2_.NAME " +
                                        "from BOOK tb_1_ " +
                                        "left join BOOK_STORE tb_2_ on tb_1_.STORE_ID = tb_2_.ID " +
                                        "where tb_1_.NAME ilike ? " +
                                        "order by tb_1_.NAME asc, tb_1_.EDITION asc " +
                                        "limit ? offset ?"
                        );
                        ctx.row(
                                0,
                                "{" +
                                        "--->\"rows\":[" +
                                        "--->--->{" +
                                        "--->--->--->\"id\":\"780bdf07-05af-48bf-9be9-f8c65236fecc\"," +
                                        "--->--->--->\"name\":\"GraphQL in Action\"," +
                                        "--->--->--->\"edition\":3," +
                                        "--->--->--->\"price\":80.00," +
                                        "--->--->--->\"store\":{" +
                                        "--->--->--->--->\"id\":\"2fa3955e-3e83-49b9-902e-0465c109c779\"," +
                                        "--->--->--->--->\"name\":\"MANNING\"" +
                                        "--->--->--->}" +
                                        "--->--->},{" +
                                        "--->--->--->\"id\":\"e110c564-23cc-4811-9e81-d587a13db634\"," +
                                        "--->--->--->\"name\":\"Learning GraphQL\"," +
                                        "--->--->--->\"edition\":1," +
                                        "--->--->--->\"price\":50.00," +
                                        "--->--->--->\"store\":{" +
                                        "--->--->--->--->\"id\":\"d38c10da-6be8-4924-b9b9-5e81899612a0\"," +
                                        "--->--->--->--->\"name\":\"O'REILLY\"" +
                                        "--->--->--->}" +
                                        "--->--->}" +
                                        "--->]," +
                                        "--->\"totalRowCount\":6," +
                                        "--->\"totalPageCount\":3" +
                                        "}"
                        );
                    }
            );
        }
    }

    @Test
    public void testMappedByProp() {
        AdministratorTable table = AdministratorTable.$;
        executeAndExpect(
                getSqlClient()
                        .createQuery(table)
                        .select(
                                table.fetch(
                                        AdministratorFetcher.$
                                                .name()
                                                .metadata(
                                                        ReferenceFetchType.JOIN_ALWAYS,
                                                        AdministratorMetadataFetcher.$
                                                                .name()
                                                )
                                )
                        ),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.ID, tb_1_.NAME, " +
                                    "tb_2_.ID, tb_2_.NAME " +
                                    "from ADMINISTRATOR tb_1_ " +
                                    "left join ADMINISTRATOR_METADATA tb_2_ on tb_1_.ID = tb_2_.ADMINISTRATOR_ID " +
                                    "where tb_1_.DELETED <> ?"
                    );
                    ctx.rows(
                            "[" +
                                    "{\"name\":\"a_1\",\"metadata\":{\"name\":\"am_1\",\"id\":10},\"id\":1}," +
                                    "{\"name\":\"a_3\",\"metadata\":{\"name\":\"am_3\",\"id\":30},\"id\":3}" +
                                    "]"
                    );
                }
        );
    }
}
