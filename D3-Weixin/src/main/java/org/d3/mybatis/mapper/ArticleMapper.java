package org.d3.mybatis.mapper;

import org.d3.mybatis.domain.Article;

public interface ArticleMapper {

  void insertArticle(Article article);
  Article getArticleByDocid(String docid);
}
