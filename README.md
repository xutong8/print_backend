传输给前端的数据按照设计稿封装后传输

注意传输传输文件的时候，添加产品或者滤饼时会将原来的关联关系全部删除

需要重新导入相关的关联数据

目的是防止数据出现配方更新但数据库中保留旧的残留关系的问题



PageNo

前端传输的页面数从1开始

后端输入时-1处理  输出数据封装时 +1 处理

后端处理数据时仍按照0开始处理



todo：





