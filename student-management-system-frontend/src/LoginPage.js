import React, { useState, useEffect } from 'react';
import { Form, Input, Button, Checkbox, Card, Typography, message, Modal, Row, Col, Tabs } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import 'antd/dist/reset.css';
import './Login.css';
import { useNavigate } from 'react-router-dom';
import api from './api';

const { Title } = Typography;
const { TabPane } = Tabs;

const LoginPage = () => {
  const [messageApi, contextHolder] = message.useMessage();
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [userType, setUserType] = useState('Student'); // Default to Admin login
  const navigate = useNavigate();
  const [rememberMe, setRememberMe] = useState(false);
  const [form] = Form.useForm();

  useEffect(() => {
    // Check if there are stored credentials
    const storedUsername = localStorage.getItem('rememberedUsername');
    const storedPassword = localStorage.getItem('rememberedPassword');
    const wasRemembered = localStorage.getItem('rememberMe') === 'true';
    
    if (wasRemembered && storedUsername && storedPassword) {
      form.setFieldsValue({
        username: storedUsername,
        password: storedPassword,
        remember: true
      });
      setRememberMe(true);
    }
  }, [form]);
  const onFinishLogin = async (values) => {
    console.log('Success:', values);
    try {
      const response = await api.post(
        userType === 'Staff'
          ? '/api/staff/login'
          : userType === 'Admin'
          ? '/api/admin/login'
          : '/api/student/login',
        {
          username: values.username,
          password: values.password,
        }
      );
      console.log(response);
      if (response.data.code === 200) {
        // Handle Remember Me
        if (values.remember) {
          localStorage.setItem('rememberedUsername', values.username);
          localStorage.setItem('rememberedPassword', values.password);
          localStorage.setItem('rememberMe', 'true');
        } else {
          // Clear remembered credentials if "Remember Me" is unchecked
          localStorage.removeItem('rememberedUsername');
          localStorage.removeItem('rememberedPassword');
          localStorage.setItem('rememberMe', 'false');
        }

        message.success('Login successful!');
        localStorage.setItem('accessToken', response.data.data.accessToken);
        localStorage.setItem('refreshToken', response.data.data.refreshToken);
        localStorage.setItem('username', values.username);
        localStorage.setItem('userRole', userType);
        if (userType === 'Staff') {
          navigate('/staff-home');
        } else if (userType === 'Admin') {
          navigate('/admin-home');
        } else {
          navigate('/student-home');
        }
      } else {
        message.error(response.data.message || 'Login failed. Please try again.');
      }
    } catch (error) {
      message.error('Login failed. Please try again.');
    }
  };

  const onFinishFailed = (errorInfo) => {
    console.log('Failed:', errorInfo);

    const errorFields = errorInfo.errorFields.map((field) => field.name[0]);
    if (errorFields.includes('username') && errorFields.includes('password')) {
      messageApi.open({
        type: 'error',
        content: 'Username and Password required',
      });
    } else if (errorFields.includes('username')) {
      noUsername();
    } else if (errorFields.includes('password')) {
      noPassword();
    }
  };

  const noUsername = () => {
    messageApi.open({
      type: 'error',
      content: 'Username required',
    });
  };

  const noPassword = () => {
    messageApi.open({
      type: 'error',
      content: 'Password required',
    });
  };

  const showForgotPasswordModal = () => {
    setIsModalVisible(true);
  };

  const handleModalCancel = () => {
    setIsModalVisible(false);
  };

  const handleUserTypeChange = (key) => {
    setUserType(key);
  };

  const handleRememberMeChange = (e) => {
    setRememberMe(e.target.checked);
    if (!e.target.checked) {
      // Clear remembered credentials when unchecking
      localStorage.removeItem('rememberedUsername');
      localStorage.removeItem('rememberedPassword');
      localStorage.setItem('rememberMe', 'false');
    }
  };

  return (
    <div className='login-container'>
      {contextHolder}
      <Card hoverable className="login-card">
        <Title level={3} className="login-title">Student Management System</Title>

        <Tabs centered defaultActiveKey="Student" onChange={handleUserTypeChange} className="tabs-container">
          <TabPane tab="Student" key="Student">
            {/* Student login form */}
          </TabPane>
          <TabPane tab="Staff" key="Staff">
            {/* Staff login form */}
          </TabPane>
          <TabPane tab="Admin" key="Admin">
            {/* Admin login form */}
          </TabPane>
        </Tabs>
        <Form
          form={form}
          name="login"
          initialValues={{ remember: rememberMe }}
          onFinish={onFinishLogin}
          onFinishFailed={onFinishFailed}
          autoComplete="off"
          className="login-form"
        >
          <Form.Item
            name="username"
            rules={[{ required: true, message: '' }]}
          >
            <Input size="large" prefix={<UserOutlined />} placeholder="Username" className="login-input" />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: '' }]}
          >
            <Input.Password size="large" prefix={<LockOutlined />} placeholder="Password" className="login-input" />
          </Form.Item>

          <Form.Item name="remember" valuePropName="checked" className="login-options">
            <Row align="middle" justify="space-between">
              <Col>
                <Checkbox 
                  className="login-checkbox" 
                  checked={rememberMe}
                  onChange={handleRememberMeChange}
                >
                  Remember me
                </Checkbox>
              </Col>
              <Col>
                <button
                  type="button"
                  className="forgot-password-button"
                  onClick={showForgotPasswordModal}
                >
                  Forgot Password?
                </button>
              </Col>
            </Row>
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" block className="login-button">
              Login
            </Button>
          </Form.Item>
        </Form>
      </Card>

      <Modal
        title="Forgot Password?"
        visible={isModalVisible}
        onCancel={handleModalCancel}
        footer={[
          <Button key="close" type="primary" onClick={handleModalCancel}>
            Close
          </Button>,
        ]}
      >
        <p>If you forgot your password, please contact the IT desk at:</p>
        <p><a href="mailto:itdesk@example.com">itdesk@example.com</a></p>
      </Modal>
    </div>
  );
};

export default LoginPage;
