package com.jlfex.hermes.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jlfex.hermes.common.utils.Numbers;
import com.jlfex.hermes.common.utils.Strings;
import com.jlfex.hermes.model.Invest;
import com.jlfex.hermes.model.InvestProfit;
import com.jlfex.hermes.model.LoanOverdue;
import com.jlfex.hermes.model.User;
import com.jlfex.hermes.repository.CommonRepository;
import com.jlfex.hermes.repository.InvestProfitRepository;
import com.jlfex.hermes.repository.LoanOverdueRepository;
import com.jlfex.hermes.service.InvestProfitService;
import com.jlfex.hermes.service.RepayService;
import com.jlfex.hermes.service.pojo.InvestProfitInfo;
import com.jlfex.hermes.service.repay.RepayMethod;

/**
 * 
 * 理财收益业务实现
 * 
 * @author chenqi
 * @version 1.0, 2013-12-24
 * @since 1.0
 */
@Service
@Transactional
public class InvestProfitServiceImpl implements InvestProfitService {

	@Autowired
	private InvestProfitRepository investProfitRepository;
	@Autowired
	private CommonRepository commonRepository;
	/** 借款逾期信息仓库 */
	@Autowired
	private LoanOverdueRepository loanOverdueRepository;
	@Autowired
	private RepayService repayService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jlfex.hermes.service.InvestProfitService#save(com.jlfex.hermes
	 * .model.InvestProfit)
	 */
	@Override
	public InvestProfit save(InvestProfit investProfit) {

		// 保存数据并返回
		return investProfitRepository.save(investProfit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jlfex.hermes.service.InvestProfitService#LoadById(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public InvestProfit loadById(String id) {
		return investProfitRepository.findOne(id);
	}

	@Override
	public BigDecimal loadSumAllProfitByUserAndInStatus(User user, String... status) {

		return investProfitRepository.loadSumAllProfitByUserAndInStatus(user, Arrays.asList(status));
	}

	@Override
	public BigDecimal loadInterestSumByUserAndInStatus(User user, String... status) {
		return investProfitRepository.loadInterestSumByUserAndInStatus(user, Arrays.asList(status));
	}

	@Override
	public BigDecimal loadOverdueInterestSumByUserAndInStatus(User user, String... status) {
		return investProfitRepository.loadOverdueInterestSumByUserAndInStatus(user, Arrays.asList(status));
	}

	// private String getCondition(String investid, Map<String, Object> params)
	// {
	// StringBuilder condition = new StringBuilder();
	// condition.append(" where 1=1");
	// if (!Strings.empty(investid)) {
	// condition.append(" and hip.id = :id");
	// params.put("id", investid);
	// }
	//
	// return condition.toString();
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jlfex.hermes.service.InvestProfitService#findByJointSql(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	// @Override
	// public Page<Map<String, Object>> findByJointSql(String investId, String
	// page, String size) {
	// Map<String, Object> params = new HashMap<String, Object>();
	//
	// String sqlSearchByInvestProfit =
	// commonRepository.readScriptFile(Script.searchByInvestProfit);
	// String sqlCountSearchByInvestProfit =
	// commonRepository.readScriptFile(Script.countSearchByInvestProfit);
	//
	// String condition = getCondition(investId, params);
	// sqlSearchByInvestProfit = String.format(sqlSearchByInvestProfit,
	// condition);
	// sqlCountSearchByInvestProfit =
	// String.format(sqlCountSearchByInvestProfit, condition);
	//
	// // 初始化
	// Pageable pageable =
	// Pageables.pageable(Integer.valueOf(Strings.empty(page, "0")),
	// Integer.valueOf(Strings.empty(size, "100")));
	// List<?> listCount =
	// commonRepository.findByNativeSql(sqlCountSearchByInvestProfit, params);
	// Long total = Long.parseLong(String.valueOf(listCount.get(0)));
	// Logger.info("total:" + total);
	// List<?> list = commonRepository.findByNativeSql(sqlSearchByInvestProfit,
	// params, pageable.getOffset(), pageable.getPageSize());
	// List<Map<String, Object>> investProfits = new ArrayList<Map<String,
	// Object>>();
	// for (int i = 0; i < list.size(); i++) {
	// Map<String, Object> attributes = new HashMap<String, Object>();
	// Object[] object = (Object[]) list.get(i);
	// attributes.put("sequence", String.valueOf(object[0]));
	// attributes.put("period", String.valueOf(object[1]));
	// attributes.put("planDatetime", String.valueOf(object[2]));
	// attributes.put("principal", String.valueOf(object[3]));
	// attributes.put("interest", String.valueOf(object[4]));
	// attributes.put("amount", String.valueOf(object[5]));
	// attributes.put("overdueInterest", String.valueOf(object[6]));
	// attributes.put("overdueDays", String.valueOf(object[7]));
	// attributes.put("status", String.valueOf(object[8]));
	// investProfits.add(attributes);
	// }
	// // 返回结果
	// Page<Map<String, Object>> pageMap = new PageImpl<Map<String,
	// Object>>(investProfits, pageable, total);
	// return pageMap;
	//
	// }

	/**
	 * 此方法用于获取已收记录及最新一期待收
	 * 
	 * @param loan
	 * @return
	 */
	public List<InvestProfitInfo> getInvestProfitRecords(Invest invest) {
		List<InvestProfit> investProfitList = investProfitRepository.findByInvest(invest);
		List<InvestProfit> investProfitRecords = new ArrayList<InvestProfit>();

		int count = investProfitList.size();
		if (count > 0) {
			InvestProfit unRepay = null;
			investProfitRecords.add(unRepay);
			for (InvestProfit investProfit : investProfitList) {
				// 假如正常还款或者逾期还款，
				if (InvestProfit.Status.ALREADY.equals(investProfit.getStatus()) || InvestProfit.Status.ADVANCE.equals(investProfit.getStatus())
						|| InvestProfit.Status.OVERDUE.equals(investProfit.getStatus())) {
					investProfitRecords.add(investProfit);
					count--;
				}
			}
			// 判断是否存在还未还款的 ，等于0表示全都已收款(包括已收款和已收逾期款和已收垫付款)了
			if (count > 0) {
				// 获取当期未还
				unRepay = investProfitList.get(count - 1);

				Date now = new Date();
				// 如果计划时间小于等于当前时间，为正常还款
				if (unRepay.getLoanRepay().getPlanDatetime().getTime() <= now.getTime()) {
					// 计算逾期天数
					int overdueDay = RepayServiceImpl.getOverdueDays(unRepay.getLoanRepay().getPlanDatetime(), now);
					// 计算逾期等级
					Integer rank = (overdueDay - 1) / 30 + 1;
					LoanOverdue loanOverdue = loanOverdueRepository.findByLoanAndRank(unRepay.getLoanRepay().getLoan(), rank);
					// 根据逾期等级获取费率，如获取为空，则表示坏账，获取为非空，采用获取费率计算
					if (loanOverdue == null) {
						loanOverdue = loanOverdueRepository.findByLoanAndRank(unRepay.getLoanRepay().getLoan(), 0);
					}
					RepayMethod repayMethod = repayService.getRepayMethod(unRepay.getLoanRepay().getLoan().getRepay().getId());
					// 计算借款人逾期违约金
					// BigDecimal overduePenalty =
					// repayMethod.getOverduePenalty(overdueDay,
					// loan.getPeriod() -
					// unRepay.getSequence() + 1, loan.getManageFee(),
					// loanOverdue.getPenalty());
					// 计算逾期罚息
					BigDecimal overdueInterest = repayMethod.getOverdueInterest(overdueDay, unRepay.getLoanRepay().getLoan().getPeriod() - unRepay.getLoanRepay().getSequence() + 1, unRepay
							.getLoanRepay().getLoan().getAmount(), loanOverdue.getInterest());
					unRepay.getLoanRepay().setOverdueDays(overdueDay);
					unRepay.setOverdueInterest(overdueInterest);
				}
				investProfitRecords.set(0, unRepay);
			} else {
				investProfitRecords.remove(0);
			}
		}
		List<InvestProfitInfo> investProfitInfoRecords = new ArrayList<InvestProfitInfo>();
		for (InvestProfit investProfit : investProfitRecords) {
			InvestProfitInfo investProfitInfo = new InvestProfitInfo();
			investProfitInfo.setSequence(Strings.toString(investProfit.getLoanRepay().getSequence()));
			investProfitInfo.setPlanDatetime(investProfit.getLoanRepay().getPlanDatetime());
			investProfitInfo.setPeriod(Strings.toString(investProfit.getLoanRepay().getLoan().getPeriod()));
			investProfitInfo.setAmount(Numbers.toCurrency(investProfit.getAmount().doubleValue()));
			investProfitInfo.setInterest(Numbers.toCurrency(investProfit.getInterest().doubleValue()));
			investProfitInfo.setPrincipal(Numbers.toCurrency(investProfit.getPrincipal().doubleValue()));
			investProfitInfo.setOverdueInterest(Numbers.toCurrency(investProfit.getOverdueInterest().doubleValue()));
			investProfitInfo.setOverdueDays(Strings.toString(investProfit.getLoanRepay().getOverdueDays()));
			investProfitInfo.setStatus(investProfit.getStatus());
			investProfitInfoRecords.add(investProfitInfo);
		}
		return investProfitInfoRecords;
	}
	
	@Override
	public InvestProfit sumAllProfitByAssignLoan(User user, String loanKind ,String... profitState) {
		return investProfitRepository.sumAllProfitByAssignLoan( Arrays.asList(profitState),loanKind, user);
	}

}
