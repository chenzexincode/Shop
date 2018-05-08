package com.shop.web.servlet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shop.service.OrderService;
import com.shop.utils.PaymentUtil;

/**
 * ��Servlet����֧���ɹ��� ���е���----- ֧����˾ ���ͻ�
 * 
 * @author seawind
 * 
 */
public class CallbackServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// ��ûص���������
		String p1_MerId = request.getParameter("p1_MerId");
		String r0_Cmd = request.getParameter("r0_Cmd");
		String r1_Code = request.getParameter("r1_Code");
		String r2_TrxId = request.getParameter("r2_TrxId");
		String r3_Amt = request.getParameter("r3_Amt");
		String r4_Cur = request.getParameter("r4_Cur");
		String r5_Pid = request.getParameter("r5_Pid");
		String r6_Order = request.getParameter("r6_Order");
		String r7_Uid = request.getParameter("r7_Uid");
		String r8_MP = request.getParameter("r8_MP");
		String r9_BType = request.getParameter("r9_BType");
		String rb_BankId = request.getParameter("rb_BankId");
		String ro_BankOrderId = request.getParameter("ro_BankOrderId");
		String rp_PayDate = request.getParameter("rp_PayDate");
		String rq_CardNo = request.getParameter("rq_CardNo");
		String ru_Trxtime = request.getParameter("ru_Trxtime");
		// ���У�� --- �ж��ǲ���֧����˾֪ͨ��
		String hmac = request.getParameter("hmac");
		String keyValue = ResourceBundle.getBundle("merchantInfo").getString(
				"keyValue");

		// �Լ����������ݽ��м��� --- �Ƚ�֧����˾������hamc
		boolean isValid = PaymentUtil.verifyCallback(hmac, p1_MerId, r0_Cmd,
				r1_Code, r2_TrxId, r3_Amt, r4_Cur, r5_Pid, r6_Order, r7_Uid,
				r8_MP, r9_BType, keyValue);
				
				
		if (isValid) {
			// ��Ӧ������Ч
			if (r9_BType.equals("1")) {
				// �޸Ķ���״̬ Ϊ�Ѹ���--------��������ַ������£�Ӧ��д�ڷ�������Ե��ͨѶ��
				OrderService service = new OrderService();
				try {
					service.updateStateByOid(r6_Order);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// ������ض���
				response.setContentType("text/html;charset=utf-8");
				response.getWriter().println("<h1>����ɹ����ȴ��̳ǽ�һ���������ȴ��ջ�...</h1>");
			} else if (r9_BType.equals("2")) {
				// ��������Ե� --- ֧����˾֪ͨ��
				System.out.println("����ɹ���");
				// �޸Ķ���״̬ Ϊ�Ѹ���
				// �ظ�֧����˾
				response.getWriter().print("success");
			}
		} else {
			// ������Ч
			System.out.println("���ݱ��۸ģ�");
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
