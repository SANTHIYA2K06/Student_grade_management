import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { UserOutlined, FormOutlined, SettingOutlined, LogoutOutlined, HomeOutlined, TeamOutlined } from '@ant-design/icons';
import { Breadcrumb, Layout, Menu, Typography, Dropdown, Modal, Form, Input, Button, message } from 'antd';
import AcademicRecords from './AcademicRecords';
import PersonalInformation from './PersonalInformation';
import './StudentHome.css';
import Home from '../Home';
import tokenService from '../tokenService';
import api, { logout } from '../api';
import ChangePasswordModal from '../components/ChangePasswordModal';

const { Header, Content, Sider } = Layout;
const { Title } = Typography;

const StudentHome = () => {
  const [selectedKey, setSelectedKey] = useState('home');
  const [breadcrumbItems, setBreadcrumbItems] = useState([{ label: 'Home', icon: <HomeOutlined /> }]);
  const navigate = useNavigate();
  const [isChangePasswordModalVisible, setIsChangePasswordModalVisible] = useState(false);
  const [changePasswordLoading, setChangePasswordLoading] = useState(false);
  const username = localStorage.getItem('username');
  const [form] = Form.useForm();

  // Logout handler
  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  useEffect(() => {
    const logoutHandler = () => {
      navigate('/login');
    };

    window.addEventListener('logout', logoutHandler);
    return () => {
      window.removeEventListener('logout', logoutHandler);
    };
  }, [navigate]);

  // Show change password modal
  const showChangePasswordModal = () => {
    setIsChangePasswordModalVisible(true);
  };

  // Handle change password
  const handleChangePassword = async (values) => {
    setChangePasswordLoading(true);
    try {
      await api.put('/api/auth/reset-password', {
        oldPassword: values.oldPassword,
        newPassword: values.newPassword
      });
      message.success('Password changed successfully');
      setIsChangePasswordModalVisible(false);
      form.resetFields();
    } catch (error) {
      console.error('Change password failed:', error);
      message.error(error.response?.data?.message || 'Failed to change password');
    } finally {
      setChangePasswordLoading(false);
    }
  };

  const menuItemsList = [
    {
      key: '1',
      icon: <TeamOutlined />,
      label: 'Personal Information',
    },
    {
      key: '2',
      icon: <FormOutlined />,
      label: 'Academic Records',
    }
  ];

  // Add updateBreadcrumb function
  const updateBreadcrumb = () => {
    const homeBreadcrumb = { label: 'Home', icon: <HomeOutlined /> };
    let newBreadcrumb = [homeBreadcrumb];

    const selectedMenuItem = menuItemsList.find((item) => item.key === selectedKey);
    if (selectedMenuItem) {
      newBreadcrumb = [
        homeBreadcrumb,
        { label: selectedMenuItem.label, icon: selectedMenuItem.icon },
      ];
    }

    setBreadcrumbItems(newBreadcrumb);
  };

  // Add useEffect to update breadcrumb
  useEffect(() => {
    updateBreadcrumb();
  }, [selectedKey]);

  const handleMenuClick = ({ key }) => {
    setSelectedKey(key);
  };

  const handleBreadcrumbClick = (breadcrumbItem) => {
    if (breadcrumbItem.label === 'Home') {
      setSelectedKey('home');
    } else {
      const menuItem = menuItemsList.find(item => item.label === breadcrumbItem.label);
      if (menuItem) {
        setSelectedKey(menuItem.key);
      }
    }
  };

  const menuItems = [
    {
      key: '1',
      label: <span onClick={showChangePasswordModal}>Change Password</span>,
      icon: <SettingOutlined />,
    },
    {
      key: '2',
      label: <span onClick={handleLogout}>Log Out</span>,
      icon: <LogoutOutlined />,
    },
  ];

  const menu = <Menu items={menuItems} />;

  const renderContent = () => {
    switch (selectedKey) {
      case '1':
        return <PersonalInformation />;
      case '2':
        return <AcademicRecords />;
      default:
        return <Home />;
    }
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header className="header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Title 
          level={4} 
          className="header-title"
          onClick={() => setSelectedKey('home')}
        >
          Awesome University
        </Title>
        <Dropdown overlay={menu} placement="bottom">
          <div style={{ display: 'flex', alignItems: 'center', cursor: 'pointer', color: 'white', margin: '20px' }}>
            <UserOutlined />
            <span style={{ marginLeft: '8px' }}>{username || 'Username'}</span>
          </div>
        </Dropdown>
      </Header>

      <Layout>
        <Sider className="sider" width={280}>
          <Menu
            mode="inline"
            selectedKeys={[selectedKey]}
            className="sider-menu"
            items={menuItemsList}
            onClick={handleMenuClick}
          />
        </Sider>
        <Layout className="content-layout">
          <Breadcrumb style={{ margin: '16px 0' }} className="breadcrumb">
            {breadcrumbItems.map((item, index) => (
              <Breadcrumb.Item
                key={index}
                onClick={() => handleBreadcrumbClick(item)}
                style={{ cursor: 'pointer', display: 'flex', alignItems: 'center' }}
              >
                {item.icon && <span style={{ marginRight: '8px' }}>{item.icon}</span>}
                {item.label}
              </Breadcrumb.Item>
            ))}
          </Breadcrumb>

          <Content className="content" style={{ background: '#fff', padding: 24 }}>
            {renderContent()}
          </Content>
        </Layout>
      </Layout>

      <ChangePasswordModal
        visible={isChangePasswordModalVisible}
        onCancel={() => setIsChangePasswordModalVisible(false)}
        onSubmit={handleChangePassword}
        form={form}
        loading={changePasswordLoading}
      />
    </Layout>
  );
};

export default StudentHome;
