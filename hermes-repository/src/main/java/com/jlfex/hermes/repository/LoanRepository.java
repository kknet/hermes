package com.jlfex.hermes.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jlfex.hermes.model.ArticleCategoryReference;
import com.jlfex.hermes.model.Loan;
import com.jlfex.hermes.model.User;

/**
 * 
 * 借款信息仓库
 * 
 * @author Ray
 * @version 1.0, 2013-12-23
 * @since 1.0
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, String> {

	/**
	 * 通过状态查询借款
	 * 
	 * @param status
	 * @return
	 */
	public List<Loan> findByStatusIn(List<String> status);
	
	/**
	 * 通过状态分页查询借款
	 * 
	 * @param status
	 * @param pageable
	 * @return
	 */
	public Page<Loan> findByStatusIn(List<String> status, Pageable pageable);
	
	
	/**
	 * 通过用户查找借款
	 * @param user
	 * @return
	 */
	@Query("from Loan  where user = ?1 order by datetime desc")
	public List<Loan> findByUser(User user);
	
	/**
	 * 按照借款编号倒叙排列	
	 * @return
	 */
	@Query("from Loan order by loanNo desc")
	public List<Loan> findAllOrderByLoanNo();
	
	
	
	
	
	/**
	 * 通过状态查询借款
	 * 
	 * @param status
	 * @return
	 */
	public List<Loan> findByStatus(String status);
	
	/**
	 * 通过用户编号查找借款
	 * 
	 * @param userId
	 * @return
	 */
	public List<Loan> findByUserIdOrderByDatetimeDesc(String userId);
	
	/**
	 * 标的： 类型 和 状态 列表
	 * @return
	 */
	public Page<Loan> findByloanKindAndStatusIn(String loanKind, List<String> status, Pageable pageable);
	
}
