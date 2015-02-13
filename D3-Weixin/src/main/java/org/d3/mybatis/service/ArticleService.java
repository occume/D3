package org.d3.mybatis.service;

import org.d3.mybatis.domain.Article;
import org.d3.mybatis.mapper.ArticleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleService {

	private static Logger LOG = LoggerFactory.getLogger(ArticleService.class);
	
	@Autowired
	private ArticleMapper articleMapper;

	@Transactional
	public void addArticle(Article article) {
		articleMapper.insertArticle(article);
	}

	public boolean exist(String docid){
		return articleMapper.getArticleByDocid(docid) != null;
	}
}
