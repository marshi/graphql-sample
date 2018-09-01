# graphql-sample

graphql-java-springのサンプル実装です。

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

クエリを投げるとこのクラスのメソッドが呼び出されます

スキーマ定義では👇のように定義されています
```
schema {
    query: Query
}

type Query {
    bloginfo(ids: [Long]): BlogListResponse
}
```
この定義だと、queryの1レベル下には `blogInfo(ids: [Long]) ~~` が定義されているため`bloginfo(ids: List<Long>)`メソッドが対象のメソッドとして検索されて実行されます
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



