# graphql-sample

graphql-spring-bootのサンプル実装です。

# 動かし方

1. 起動
1. http://localhost:8080/graphql を指定
1. 下記クエリを実行
```
query {
  bloginfo(ids:[1,2,3]) {
    blogs {
      id
      name
      entries {
        title
      }
    }
  }
}
```

# 説明

## スキーマ定義ファイル

https://github.com/marshi/graphql-sample/blob/v1.0/src/main/resources/schema.graphqls


graphql-java-toolsによって、自動的に`*.graphqls` というファイルがスキーマ定義として選ばれます

https://github.com/graphql-java/graphql-spring-boot#graphql-java-tools

## GraphQLQueryResolver
https://github.com/marshi/graphql-sample/blob/v1.0/src/main/kotlin/marshi/graphqlsample/resolver/QueryResolver.kt

GraphQLにはquery, mutation, subscriptionという操作が存在しますがそれぞれに対応したResolverが用意されています

GraphQLQueryResolver, GraphQLMutationResolver, GraphQLSubscriptionResolverです

今回はqueryしか使っていないのでGraphQLQueryResolverを継承したResolverを作成します

https://github.com/marshi/graphql-sample/blob/v1.0/src/main/kotlin/marshi/graphqlsample/resolver/QueryResolver.kt

Bean管理する必要があるので、@Componentをつけておきます

クエリをリクエストするとこのクラスのメソッドが呼び出されます

ところでスキーマ定義では👇のように定義されています
```
schema {
    query: Query
}

type Query {
    bloginfo(ids: [Long]): BlogListResponse
}
```
この定義だとqueryの1レベル下には `blogInfo(ids: [Long]) ~~` が定義されているため、まず`bloginfo(ids: List<Long>)`メソッドが対象のメソッドとして検索されて実行されます
https://github.com/graphql-java/graphql-java-tools#field-mapping-priority

```
@Component
class QueryResolver : GraphQLQueryResolver{
  fun bloginfo(ids: List<Long>) = BlogListResponseResolver(ids)
}
```
ここでは別のResolverを返していますが、レスポンスを表すクラスや、CompletableFutureで包んだクラスを返しても大丈夫です

## GraphQLResolver

さきほどは`GraphQLQueryResolver`を継承しましたが、queryの直下以外のResolverには`GraphQLResolver`を継承したクラスを作成します

https://github.com/marshi/graphql-sample/blob/master/src/main/kotlin/marshi/graphqlsample/resolver/field/BlogListResponseResolver.kt

さきほどと同様にスキーマ定義と対応したblogsメソッドが呼びだされます

ただし、Resolverのメソッドの引数としてDataFetchingEnvironment型のデータを受け取ることができます
https://github.com/graphql-java/graphql-java-tools#field-mapping-priority

1回のリクエストで共有される情報がいろいろ含まれますが、今回は特にdataloaderの取得に使っています

dataloaderについては後述します

ここで注目したいのが、スキーマで定義されている型と対応したクラスの持つフィールドの違いです

スキーマでは下記のように定義されています
```
type Blog {
    id: Long
    name: String
    entries: [Entry]
}
```
しかしクラスでは下記の様に定義されています
```
data class Blog(
    var id: Long,
    var name: String
)
```

entriesがクラスには定義されていません

この場合、スキーマのentriesを解決するために`GraphQLResolver<Blog>`を継承したResolverからentriesメソッドが探されます(これで見つからないと起動エラーになります)

https://github.com/marshi/graphql-sample/blob/master/src/main/kotlin/marshi/graphqlsample/resolver/field/BlogResolver.kt

このときentriesメソッドでは、Entryから見た一つ上のレベルであるBlog型のデータを引数として受け取ることができます

つまり、このケースでいうとBlogのidに依存するEntryを取得する場合、BlogとEntryはスキーマの親子関係である必要があります

そんなこんなで順々に各フィールドがResolverによって解決されていき、最終的なレスポンスができあがります

## Dataloader

https://github.com/graphql-java/java-dataloader

GraphQLを使うと発生しやすい問題としてN+1問題が挙げられます

複数ブログを一括で取得する -> 各ブログについて記事情報を取得する、

というシナリオだと最初の複数のブログ取得するで1回、それぞれの記事を取得するでN回のリクエストが発生するので、通信が多くなるという問題です

GraphQLではフィールドについてデータの解決が行われるため、この問題が起こりやすいのです

Dataloaderはこの問題を解決します(あとキャッシュ機能もあるみたいです)

図で雑に書くとこんな感じです

<img src="https://user-images.githubusercontent.com/1423942/44948644-7c463400-ae5c-11e8-8d5a-8d1217c9a363.png" width="700"/>

Dataloaderが個別のリクエストを溜めておきその後一括で取得した後、個別にデータを返します

これによってResolverがデータを取得するときには個別でデータを取得しているような感覚ですが、実はまとめてリクエストが飛ばされているためN+1問題が解決されます

ただし、これを実現するためにはBackend Serverで複数のデータを一括で取得できるAPIを実装する必要があります

さらに、各データにはIDが振られている必要もあります

## Dataloader実装

サンプルではCustomGraphQLContextBuilder内でDataLoaderRegistryにDataloaderの登録をしています
https://github.com/marshi/graphql-sample/blob/24bcea5644a800308e6ad7a3459aff1cb26ae9aa/src/main/kotlin/marshi/graphqlsample/CustomGraphQLContextBuilder.kt#L36-L39

DataLoadにはBatchLoaderを渡します

BatchLoaderが一括取得の処理を担います

このBatchLoaderによって得られたデータ一覧の保持やキーに対応するデータの受け渡しをDataloaderが担います

DataLoaderRegistryに登録されたDataloaderは各ResolverのDataFetchingEnvironmentから受け取ることができます

ResolverではDataloaderを経由してデータを取得することで図のような一括処理ができるようになります







