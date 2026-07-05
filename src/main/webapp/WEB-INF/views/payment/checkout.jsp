<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>결제 | BookShop</title>
    <script src="https://js.tosspayments.com/v2/standard"></script>
</head>
<body>

<script>
    const clientKey = "${clientKey}";

    const tossPayments = TossPayments(clientKey);

    const payment = tossPayments.payment({
        customerKey: "${checkout.customerKey}"
    });

    payment.requestPayment({
        method: "CARD",
        amount: {
            currency: "KRW",
            value: ${checkout.amount}
        },
        orderId: "${checkout.tossOrderId}",
        orderName: "${checkout.orderName}",
        successUrl: window.location.origin + "${contextPath}/payments/toss/success",
        failUrl: window.location.origin + "${contextPath}/payments/toss/fail",
        customerName: "${checkout.customerName}"
    }).catch(function (error) {
        const message = encodeURIComponent(
            error.message || "결제가 취소되었습니다."
        );

        window.location.href = "${contextPath}/payments/toss/fail"
        + "?orderId=${checkout.tossOrderId}"
        + "&message=" + message;

    });
</script>

</body>
</html>