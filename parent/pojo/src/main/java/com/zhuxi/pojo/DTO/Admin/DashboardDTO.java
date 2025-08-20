package com.zhuxi.pojo.DTO.Admin;

public class DashboardDTO {

    private Long userCount;
    private Long adminCount;
    private Long productCount;
    private Long orderCount;
    private Long articleCount;
    private Long incomeCount;
    private Long expenseCount;
    private Long profitCount;

    public DashboardDTO() {
    }

    public DashboardDTO(Long userCount, Long adminCount, Long productCount, Long orderCount, Long articleCount, Long incomeCount, Long expenseCount, Long profitCount) {
        this.userCount = userCount;
        this.adminCount = adminCount;
        this.productCount = productCount;
        this.orderCount = orderCount;
        this.articleCount = articleCount;
        this.incomeCount = incomeCount;
        this.expenseCount = expenseCount;
        this.profitCount = profitCount;
    }

    public Long getUserCount() {
        return userCount;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    public Long getAdminCount() {
        return adminCount;
    }

    public void setAdminCount(Long adminCount) {
        this.adminCount = adminCount;
    }

    public Long getProductCount() {
        return productCount;
    }

    public void setProductCount(Long productCount) {
        this.productCount = productCount;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public Long getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(Long articleCount) {
        this.articleCount = articleCount;
    }

    public Long getIncomeCount() {
        return incomeCount;
    }

    public void setIncomeCount(Long incomeCount) {
        this.incomeCount = incomeCount;
    }

    public Long getExpenseCount() {
        return expenseCount;
    }

    public void setExpenseCount(Long expenseCount) {
        this.expenseCount = expenseCount;
    }

    public Long getProfitCount() {
        return profitCount;
    }

    public void setProfitCount(Long profitCount) {
        this.profitCount = profitCount;
    }
}
