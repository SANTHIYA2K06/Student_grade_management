import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  UserOutlined,
  FormOutlined,
  SettingOutlined,
  LogoutOutlined,
  HomeOutlined,
  TeamOutlined,
} from '@ant-design/icons';
import { Breadcrumb, Layout, Menu, Typography, Dropdown, message, Form } from 'antd';
import StudentManagement from './StudentManagement';
import StudentRecord from './StudentRecord';
import './StaffHome.css';
import ModuleManagement from './ModuleManagement';
import AcademicStaff from './AcademicStaff';
import Home from '../Home';
import GradeRecords from './GradeRecords';
import RecordDetail from './RecordDetail';
import ModuleDetail from './ModuleDetail';
import api, { logout } from '../api';
import AcademicStaffRecord from './AcademicStaffRecord';
import ChangePasswordModal from '../components/ChangePasswordModal';

const { Header, Content, Sider } = Layout;
const { Title } = Typography;

const AdminHome = () => {
  const [selectedKey, setSelectedKey] = useState('home');
  const [breadcrumbItems, setBreadcrumbItems] = useState([{ label: 'Home', icon: <HomeOutlined /> }]);
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [isChangePasswordModalVisible, setIsChangePasswordModalVisible] = useState(false);
  const [changePasswordLoading, setChangePasswordLoading] = useState(false);
  const [moduleInfo, setModuleInfo] = useState(null);
  const [staffInfo, setStaffInfo] = useState(null);
  const navigate = useNavigate();
  const username = localStorage.getItem('username');
  const [form] = Form.useForm();
  

  const menuItemsList = [
    { key: '1', icon: <FormOutlined />, label: 'Grade Record' },
    { key: '2', icon: <UserOutlined />, label: 'Student Management' },
    { key: '3', icon: <SettingOutlined />, label: 'Module Management' },
    { key: '4', icon: <TeamOutlined />, label: 'Academic Staff' },
  ];

  const updateBreadcrumb = () => {
    const homeBreadcrumb = { label: 'Home', icon: <HomeOutlined /> };
    let newBreadcrumb = [homeBreadcrumb];

    if (Array.isArray(selectedKey)) {
      if (selectedKey[0] === 'recordDetail') {
        newBreadcrumb = [
          homeBreadcrumb,
          { label: 'Grade Record', icon: <FormOutlined /> },
          { label: `Record ${selectedKey[1]}` }
        ];
      } else if (selectedKey[0] === 'moduleDetail') {
        newBreadcrumb = [
          homeBreadcrumb,
          { label: 'Module Management', icon: <SettingOutlined /> },
          { label: moduleInfo ? `${moduleInfo.code} ${moduleInfo.name}` : 'Loading...' }
        ];
      } else if (selectedKey[0] === 'staffDetail') {
        newBreadcrumb = [
          homeBreadcrumb,
          { label: 'Academic Staff', icon: <TeamOutlined /> },
          { label: staffInfo ? `${staffInfo.id} ${staffInfo.firstName} ${staffInfo.lastName}` : 'Loading...' }
        ];
      }
    } else if (selectedKey === 'student-record' && selectedStudent) {
      newBreadcrumb = [
        homeBreadcrumb,
        { label: 'Student Management', icon: <UserOutlined /> },
        { label: `${selectedStudent.id} ${selectedStudent.firstName} ${selectedStudent.lastName}` },
      ];
    } else {
      const selectedMenuItem = menuItemsList.find((item) => item.key === selectedKey);
      if (selectedMenuItem) {
        newBreadcrumb = [
          homeBreadcrumb,
          { label: selectedMenuItem.label, icon: selectedMenuItem.icon },
        ];
      }
    }

    setBreadcrumbItems(newBreadcrumb);
  };

  useEffect(() => {
    updateBreadcrumb();
  }, [selectedKey, selectedStudent, moduleInfo, staffInfo]);

  const handleMenuClick = ({ key }) => {
    setSelectedKey(key);
    setSelectedStudent(null);
  };

  const handleStudentClick = (student) => {
    setSelectedKey('student-record');
    setSelectedStudent(student);
  };

  const handleBreadcrumbClick = (breadcrumbItem) => {
    if (breadcrumbItem.label === 'Home') {
      setSelectedKey('home');
      setSelectedStudent(null);
    } else if (breadcrumbItem.label === 'Student Management') {
      setSelectedKey('2');
      setSelectedStudent(null);
    } else if (breadcrumbItem.label === 'Grade Record') {
      setSelectedKey('1');
    } else if (breadcrumbItem.label === 'Module Management') {
      setSelectedKey('3');
    } else if (breadcrumbItem.label === 'Academic Staff') {
      setSelectedKey('4');
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

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

  const renderContent = () => {
    if (Array.isArray(selectedKey)) {
      switch (selectedKey[0]) {
        case 'recordDetail':
          return <RecordDetail 
            recordId={selectedKey[1]} 
            setSelectedKey={setSelectedKey} 
            onStudentClick={handleStudentClick}
          />;
        case 'moduleDetail':
          return <ModuleDetail 
            moduleId={selectedKey[1]} 
            setSelectedKey={setSelectedKey}
            setModuleInfo={setModuleInfo}
          />;
        case 'staffDetail':
          return <AcademicStaffRecord 
            staffId={selectedKey[1]} 
            setSelectedKey={setSelectedKey}
            setStaffInfo={setStaffInfo}
          />;
        default:
          return null;
      }
    }
    
    switch (selectedKey) {
      case 'student-record':
        return <StudentRecord studentId={selectedStudent.id} setSelectedKey={setSelectedKey} />;
      case '1':
        return <GradeRecords 
          setSelectedKey={setSelectedKey} 
          onStudentClick={handleStudentClick}
        />;
      case '2':
        return <StudentManagement onStudentClick={handleStudentClick} />;
      case '3':
        return <ModuleManagement setSelectedKey={setSelectedKey} />;
      case '4':
        return <AcademicStaff setSelectedKey={setSelectedKey} />;
      default:
        return <Home />;
    }
  };

  const dropdownMenu = [
    {
      key: '1',
      label: <span onClick={() => setIsChangePasswordModalVisible(true)}>Change Password</span>,
      icon: <SettingOutlined />,
    },
    {
      key: '2',
      label: <span onClick={handleLogout}>Log Out</span>,
      icon: <LogoutOutlined />,
    },
  ];


  return (
    <Layout className="layout-container">
      <Header className="header">
        <Title 
          level={4} 
          className="header-title"
          onClick={() => {
            setSelectedKey('home');
            setSelectedStudent(null);
          }}
        >
          Awesome University
        </Title>
        <Dropdown menu={{ items: dropdownMenu }} placement="bottomRight">
          <div className="header-dropdown">
            <UserOutlined />
            <span className="header-username">{username || 'Username'}</span>
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
          <Breadcrumb className="breadcrumb">
            {breadcrumbItems.map((item, index) => (
              <Breadcrumb.Item
                key={index}
                onClick={() => handleBreadcrumbClick(item)}
                className="breadcrumb-item"
              >
                {item.icon && <span className="breadcrumb-icon">{item.icon}</span>}
                {item.label}
              </Breadcrumb.Item>
            ))}
          </Breadcrumb>

          <Content className="main-content">
            {renderContent()}
          </Content>
        </Layout>
      </Layout>

    <ChangePasswordModal
        open={isChangePasswordModalVisible}
        onCancel={() => setIsChangePasswordModalVisible(false)}
        onSubmit={handleChangePassword}
        form={form}
        loading={changePasswordLoading}
      />
    </Layout>
  );
};

export default AdminHome;